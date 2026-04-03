package domain.repository

import domain.model.StockTick
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    /**
     * Pushes a list of stock symbols to the server to trigger an Echo response.
     */
    suspend fun pushSymbols(symbols: List<String>)

    /**
     * Observes the stream of stock updates coming back from the server.
     */
    fun observeStockUpdates(): Flow<List<StockTick>>
}