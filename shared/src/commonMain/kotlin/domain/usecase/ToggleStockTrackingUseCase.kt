package domain.usecase

import domain.provider.ConfigProvider
import domain.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UseCase to handle the logic of starting or stopping the price feed.
 * It manages the [isTracking] state as a business rule.
 */
class ToggleStockTrackingUseCase(
    private val repository: StockRepository,
    private val configProvider: ConfigProvider
) {
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    suspend operator fun invoke() {
        if (_isTracking.value) {
            repository.disconnect()
            _isTracking.value = false
        } else {
            val symbols = configProvider.getSymbols()
            repository.connect(symbols)
            _isTracking.value = true
        }
    }
}