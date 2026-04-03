package domain.repository

import domain.model.StockTick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Clean Repository Interface.
 * It only knows about Domain Models and Connection State.
 */
interface StockRepository {
    val isConnected: StateFlow<Boolean>

    suspend fun connect()
    fun disconnect()
    
    /**
     * Sends a list of stock ticks. 
     * The implementation will handle the conversion to the wire format.
     */
    suspend fun sendTicks(ticks: List<StockTick>)

    /**
     * Stream of ticks received from the server.
     */
    fun observeStockUpdates(): Flow<List<StockTick>>
}