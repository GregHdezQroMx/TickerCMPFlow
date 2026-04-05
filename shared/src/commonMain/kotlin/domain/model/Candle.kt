package domain.model

/**
 * Represents a single OHLC (Open, High, Low, Close) candle for the chart.
 */
data class Candle(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)
