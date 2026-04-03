package domain.usecase

import domain.model.StockTick
import domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetStockUpdatesUseCase(private val repository: StockRepository) {
    /**
     * Executes the flow observation and sorts symbols alphabetically
     * to prevent "jumping" rows in the UI.
     */
    operator fun invoke(): Flow<List<StockTick>> =
        repository.observeStockUpdates().map { ticks ->
            ticks.sortedBy { it.symbol }
        }
}