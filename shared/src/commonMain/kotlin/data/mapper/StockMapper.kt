package data.mapper

import domain.model.StockTick
import kotlin.math.round
import kotlin.time.Clock

/**
 * Transforms WSS Raw String: "AAPL,312.81,4.66|GOOGL,107.72,3.52"
 * Into: List<StockTick> with 2-decimal precision and current timestamp.
 */
fun String.toStockTicks(): List<StockTick> {
    if (this.isBlank()) return emptyList()

    val currentTimestamp = Clock.System.now().toEpochMilliseconds()

    return this.split("|").mapNotNull { rawTick ->
        try {
            val parts = rawTick.split(",")
            if (parts.size < 3) return@mapNotNull null

            StockTick(
                symbol = parts[0],
                price = parts[1].toDouble().roundTo(2),
                changePercentage = parts[2].toDouble().roundTo(2),
                timestamp = currentTimestamp
            )
        } catch (e: Exception) {
            null
        }
    }
}

private fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}