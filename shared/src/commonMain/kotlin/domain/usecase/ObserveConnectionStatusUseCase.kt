package domain.usecase

import domain.repository.StockRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Use case to observe the current WebSocket connection status.
 */
class ObserveConnectionStatusUseCase(private val repository: StockRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.isConnected
}