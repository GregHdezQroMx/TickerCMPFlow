package domain.repository

import domain.model.StockTick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Robust Data Gateway. 
 * Connect returns a success/failure status to avoid UI hangs.
 */
interface StockRepository {
    val isConnected: StateFlow<Boolean>

    /**
     * @return true if connection established, false otherwise.
     */
    suspend fun connect(): Boolean
    
    fun disconnect()
    
    suspend fun sendTicks(ticks: List<StockTick>)

    fun observeStockUpdates(): Flow<List<StockTick>>
}