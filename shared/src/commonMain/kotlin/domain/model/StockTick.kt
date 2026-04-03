package domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a real-time price update for a specific asset.
 * @property symbol The ticker identifier (e.g., AAPL, BTC).
 * @property price The current market value in USD.
 * @property changePercentage The variation compared to the previous tick.
 * @property timestamp The exact time the tick was generated.
 */
@Serializable
data class StockTick(
    val symbol: String,
    val price: Double,
    val changePercentage: Double,
    val timestamp: Long
)


