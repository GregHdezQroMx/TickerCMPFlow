package domain.usecase

import domain.model.StockTick
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile
import kotlin.random.Random

/**
 * UseCase to handle the logic of starting or stopping the price feed.
 * Implements Exponential Backoff, Lifecycle-aware resilience and Fast Persistent Error detection.
 */
class ToggleStockTrackingUseCase(
    private val repository: StockRepository,
    private val configProvider: ConfigProvider
) {
    private val tag = "ToggleUseCase"
    private val _isTrackingEnabled = MutableStateFlow(false)
    val isTrackingEnabled: StateFlow<Boolean> = _isTrackingEnabled.asStateFlow()

    private val _isReconnecting = MutableStateFlow(false)
    val isReconnecting: StateFlow<Boolean> = _isReconnecting.asStateFlow()

    private val _isPersistentError = MutableStateFlow(false)
    val isPersistentError: StateFlow<Boolean> = _isPersistentError.asStateFlow()

    @Volatile
    private var isAppActive = true 
    private val lifecycleMutex = Mutex()
    
    private var trackingJob: Job? = null
    private var retryJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val lastPrices = mutableMapOf<String, Double>()

    init {
        Napier.d(tag = tag) { "🚀 UseCase Initialized" }
        repository.isConnected
            .onEach { connected ->
                Napier.d(tag = tag) { "🔗 Repository isConnected changed: $connected (Tracking: ${_isTrackingEnabled.value}, Reconnecting: ${_isReconnecting.value}, AppActive: $isAppActive)" }
                if (!connected && _isTrackingEnabled.value && !_isReconnecting.value && isAppActive) {
                    Napier.w(tag = tag) { "⚠️ WebSocket disconnected. Starting auto-retry..." }
                    stopPhysicalTracking()
                    startAutoReconnect()
                }
            }
            .launchIn(scope)
    }

    suspend operator fun invoke() {
        lifecycleMutex.withLock {
            Napier.d(tag = tag) { "👇 Invoke called. Current tracking enabled: ${_isTrackingEnabled.value}" }
            if (_isTrackingEnabled.value) {
                stop()
            } else {
                start()
            }
        }
    }

    suspend fun resumeIfEnabled() = lifecycleMutex.withLock {
        isAppActive = true
        Napier.d(tag = tag) { "🔼 resumeIfEnabled called. TrackingEnabled: ${_isTrackingEnabled.value}, RepoConnected: ${repository.isConnected.value}" }
        if (_isTrackingEnabled.value) {
            val needsStart = trackingJob?.isActive != true && retryJob?.isActive != true
            if (needsStart) {
                Napier.i(tag = tag) { "🔄 Forcing reconnection after resume/deep link." }
                val success = startPhysicalTracking()
                if (!success) {
                    startAutoReconnect()
                }
            }
        }
    }

    suspend fun pausePhysically() = lifecycleMutex.withLock {
        isAppActive = false
        Napier.w(tag = tag) { "🔽 pausePhysically called. TrackingEnabled: ${_isTrackingEnabled.value}" }
        stopPhysicalTracking()
        retryJob?.cancel() 
        _isReconnecting.value = false
    }

    private suspend fun start() {
        Napier.d(tag = tag) { "🚀 Starting tracking engine..." }
        _isTrackingEnabled.value = true
        _isPersistentError.value = false
        val success = startPhysicalTracking()
        if (!success && isAppActive) {
            startAutoReconnect()
        }
    }

    private suspend fun stop() {
        Napier.d(tag = tag) { "🛑 Stopping tracking engine..." }
        _isTrackingEnabled.value = false
        _isReconnecting.value = false
        _isPersistentError.value = false
        retryJob?.cancel()
        stopPhysicalTracking()
    }

    private fun startAutoReconnect() {
        if (retryJob?.isActive == true || !isAppActive) return
        
        retryJob = scope.launch {
            _isReconnecting.value = true
            var delayMs = 2000L
            var totalAttempts = 0
            
            while (isActive && _isTrackingEnabled.value && isAppActive) {
                totalAttempts++
                
                if (totalAttempts >= 5) {
                    _isPersistentError.value = true
                    Napier.e(tag = tag) { "🚨 Persistent connection error detected (Attempt #$totalAttempts)." }
                }

                Napier.d(tag = tag) { "🔄 Retry #$totalAttempts in ${delayMs}ms..." }
                delay(delayMs)
                
                if (startPhysicalTracking()) {
                    _isReconnecting.value = false
                    _isPersistentError.value = false
                    Napier.i(tag = tag) { "✅ Auto-reconnect successful after $totalAttempts attempts." }
                    return@launch
                }
                
                delayMs = (delayMs * 2).coerceAtMost(30000L)
            }
            _isReconnecting.value = false
        }
    }

    private suspend fun startPhysicalTracking(): Boolean {
        if (trackingJob?.isActive == true) return true
        
        val connected = repository.connect()
        if (!connected) return false
        
        val symbols = configProvider.getSymbols()
        Napier.d(tag = tag) { "📡 Starting ticker loop for ${symbols.size} symbols" }

        trackingJob = scope.launch {
            while (isActive) {
                val mockTicks = symbols.map { symbol ->
                    val currentBase = lastPrices.getOrPut(symbol) { Random.nextDouble(50.0, 500.0) }
                    val changePercent = Random.nextDouble(-1.5, 1.5)
                    val newPrice = currentBase * (1 + (changePercent / 100))
                    lastPrices[symbol] = newPrice

                    StockTick(symbol, newPrice, changePercent, 0L)
                }
                repository.sendTicks(mockTicks)
                delay(2000)
            }
        }
        return true
    }

    private fun stopPhysicalTracking() {
        trackingJob?.cancel()
        trackingJob = null
        repository.disconnect()
    }
}
