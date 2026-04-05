package domain.usecase

import domain.model.StockTick
import domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetStockUpdatesUseCase(private val repository: StockRepository) {
    /**
     * Executes the flow observation and sorts symbols by price (Highest first).
     * Added a secondary sort by Symbol to ensure stability during price ties
     * and prevent UI jumping.
     */
    operator fun invoke(): Flow<List<StockTick>> =
        repository.observeStockUpdates().map { ticks ->
            ticks.sortedWith(
                compareByDescending<StockTick> { it.price }
                    .thenBy { it.symbol }
            )
        }
}