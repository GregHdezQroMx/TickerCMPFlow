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
 * Ensures the Switch and Connection Indicator are always in sync by observing the Repository.
 */
class ToggleStockTrackingUseCase(
    private val repository: StockRepository,
    private val configProvider: ConfigProvider
) {
    private val _isTrackingEnabled = MutableStateFlow(false)
    val isTrackingEnabled: StateFlow<Boolean> = _isTrackingEnabled.asStateFlow()

    private var trackingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val lastPrices = mutableMapOf<String, Double>()

    init {
        // Atomic Sync: If physical connection is lost, revert user intent immediately
        repository.isConnected
            .onEach { connected ->
                if (!connected && _isTrackingEnabled.value) {
                    Napier.w(tag = "ToggleUseCase") { "⚠️ WebSocket connection lost. Reverting switch." }
                    stopPhysicalTracking()
                    _isTrackingEnabled.value = false
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
        if (_isTrackingEnabled.value) {
            startPhysicalTracking()
        }
    }

    fun pausePhysically() {
        stopPhysicalTracking()
    }

    private suspend fun start() {
        _isTrackingEnabled.value = true
        val success = startPhysicalTracking()
        if (!success) {
            _isTrackingEnabled.value = false
            Napier.e(tag = "ToggleUseCase") { "❌ Failed to connect. Aborting tracking." }
        }
    }

    private fun stop() {
        _isTrackingEnabled.value = false
        stopPhysicalTracking()
    }

    private suspend fun startPhysicalTracking(): Boolean {
        if (trackingJob?.isActive == true) return true
        
        // Block until connection is established or timeout (Deterministic)
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
