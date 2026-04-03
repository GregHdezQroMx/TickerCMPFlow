package domain.model

/**
 * Static information for a specific stock symbol.
 */
data class StockMetadata(
    val symbol: String,
    val name: String,
    val description: String,
    val marketCap: String = "N/A",
    val peRatio: String = "N/A"
)
