package domain.repository

import domain.model.StockTick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface StockRepository {
    /**
     * Connection status indicator.
     */
    val isConnected: StateFlow<Boolean>

    /**
     * Status of the price feed (Started/Stopped).
     */
    val isTracking: StateFlow<Boolean>

    /**
     * Starts the price feed for the given symbols.
     */
    suspend fun startTracking(symbols: List<String>)

    /**
     * Stops the price feed and closes the connection.
     */
    fun stopTracking()

    /**
     * Observes the stream of stock updates.
     */
    fun observeStockUpdates(): Flow<List<StockTick>>
}