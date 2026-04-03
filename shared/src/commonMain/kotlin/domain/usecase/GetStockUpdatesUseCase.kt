package domain.usecase

import domain.model.StockTick
import domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetStockUpdatesUseCase(private val repository: StockRepository) {
    /**
     * Executes the flow observation and sorts symbols by price (Highest first)
     * as required by the technical challenge.
     */
    operator fun invoke(): Flow<List<StockTick>> =
        repository.observeStockUpdates().map { ticks ->
            ticks.sortedByDescending { it.price }
        }
}