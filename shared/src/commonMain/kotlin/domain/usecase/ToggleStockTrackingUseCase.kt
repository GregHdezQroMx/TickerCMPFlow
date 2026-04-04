package domain.usecase

import domain.model.StockTick
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/**
 * UseCase to handle the logic of starting or stopping the price feed.
 * Manages both the user intent (isTracking) and the actual execution (Job).
 */
class ToggleStockTrackingUseCase(
    private val repository: StockRepository,
    private val configProvider: ConfigProvider
) {
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private var trackingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend operator fun invoke() {
        if (_isTracking.value) {
            stopTracking()
        } else {
            startTracking()
        }
    }

    suspend fun startTracking() {
        if (trackingJob?.isActive == true) return
        
        _isTracking.value = true
        repository.connect()
        
        val symbols = configProvider.getSymbols()

        trackingJob = scope.launch {
            while (isActive) {
                val mockTicks = symbols.map { symbol ->
                    StockTick(
                        symbol = symbol,
                        price = Random.nextDouble(100.0, 1000.0),
                        changePercentage = Random.nextDouble(-5.0, 5.0),
                        timestamp = 0L 
                    )
                }
                repository.sendTicks(mockTicks)
                delay(2000)
            }
        }
    }

    fun stopTracking() {
        _isTracking.value = false
        trackingJob?.cancel()
        trackingJob = null
        repository.disconnect()
    }
}