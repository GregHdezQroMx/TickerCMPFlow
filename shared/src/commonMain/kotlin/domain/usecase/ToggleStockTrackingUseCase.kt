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
import kotlin.random.Random

/**
 * UseCase to handle the logic of starting or stopping the price feed.
 * Implements Exponential Backoff, Lifecycle-aware resilience and Fast Persistent Error detection.
 */
class ToggleStockTrackingUseCase(
    private val repository: StockRepository,
    private val configProvider: ConfigProvider
) {
    private val _isTrackingEnabled = MutableStateFlow(false)
    val isTrackingEnabled: StateFlow<Boolean> = _isTrackingEnabled.asStateFlow()

    private val _isReconnecting = MutableStateFlow(false)
    val isReconnecting: StateFlow<Boolean> = _isReconnecting.asStateFlow()

    private val _isPersistentError = MutableStateFlow(false)
    val isPersistentError: StateFlow<Boolean> = _isPersistentError.asStateFlow()

    private var isAppActive = true 
    private var trackingJob: Job? = null
    private var retryJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val lastPrices = mutableMapOf<String, Double>()

    init {
        repository.isConnected
            .onEach { connected ->
                if (!connected && _isTrackingEnabled.value && !_isReconnecting.value && isAppActive) {
                    Napier.w(tag = "ToggleUseCase") { "⚠️ WebSocket disconnected. Starting auto-retry..." }
                    stopPhysicalTracking()
                    startAutoReconnect()
                }
            }
            .launchIn(scope)
    }

    suspend operator fun invoke() {
        if (_isTrackingEnabled.value) {
            stop()
        } else {
            start()
        }
    }

    suspend fun resumeIfEnabled() {
        isAppActive = true
        if (_isTrackingEnabled.value && !repository.isConnected.value) {
            Napier.d(tag = "ToggleUseCase") { "🔼 App Resumed: Restoring connection with safety delay..." }
            delay(500) 
            startPhysicalTracking()
        }
    }

    fun pausePhysically() {
        isAppActive = false
        Napier.w(tag = "ToggleUseCase") { "🔽 App Backgrounded: Silencing all network activity." }
        stopPhysicalTracking()
        retryJob?.cancel() 
        _isReconnecting.value = false
    }

    private suspend fun start() {
        _isTrackingEnabled.value = true
        _isPersistentError.value = false
        val success = startPhysicalTracking()
        if (!success && isAppActive) {
            startAutoReconnect()
        }
    }

    private fun stop() {
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
                
                // Tech Lead Note: Flag persistent error after 30s of total failure (Attempt #5)
                if (totalAttempts >= 5) {
                    _isPersistentError.value = true
                    Napier.e(tag = "ToggleUseCase") { "🚨 Persistent connection error detected (Attempt #$totalAttempts)." }
                }

                Napier.d(tag = "ToggleUseCase") { "🔄 Retry #$totalAttempts in ${delayMs}ms..." }
                delay(delayMs)
                
                if (startPhysicalTracking()) {
                    _isReconnecting.value = false
                    _isPersistentError.value = false
                    Napier.i(tag = "ToggleUseCase") { "✅ Auto-reconnect successful after $totalAttempts attempts." }
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

        trackingJob = scope.launch {
            while (isActive) {
                val mockTicks = symbols.map { symbol ->
                    val currentBase = lastPrices.getOrPut(symbol) { Random.nextDouble(50.0, 500.0) }
                    val changePercent = Random.nextDouble(-1.5, 1.5)
                    val newPrice = currentBase * (1 + (changePercent / 100))
                    lastPrices[symbol] = newPrice

                    StockTick(
                        symbol = symbol,
                        price = newPrice,
                        changePercentage = changePercent,
                        timestamp = 0L 
                    )
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
