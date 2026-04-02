package com.jght.business.stockmarket.ticker_cmp_flow.domain.model

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

/**
 * Master list of the 25 required stock and crypto symbols.
 */
val StockSymbols = listOf(
    "AAPL", "GOOGL", "MSFT", "AMZN", "TSLA",
    "META", "NVDA", "NFLX", "PYPL", "BABA",
    "DIS", "V", "BTC", "ETH", "BNB",
    "SOL", "ADA", "DOT", "MATIC", "LINK",
    "AVAX", "LTC", "GOLD", "SILVER", "OIL"
)


