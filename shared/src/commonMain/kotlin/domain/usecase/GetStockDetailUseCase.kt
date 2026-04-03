package domain.usecase

import domain.model.StockTick
import domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UseCase to filter the global stock stream for a specific symbol.
 */
class GetStockDetailUseCase(private val repository: StockRepository) {
    operator fun invoke(symbol: String): Flow<StockTick?> =
        repository.observeStockUpdates().map { ticks ->
            ticks.find { it.symbol.equals(symbol, ignoreCase = true) }
        }
}