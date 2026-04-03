package data.mapper

import domain.model.StockTick
import kotlin.math.round
import kotlin.time.Clock

/**
 * Transforms WSS Raw String: "AAPL,312.81,4.66|GOOGL,107.72,3.52"
 * Into: List<StockTick>
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

/**
 * Transforms List<StockTick> into Wire Format String for Echo Server.
 */
fun List<StockTick>.toWireFormat(): String {
    return this.joinToString("|") { tick ->
        "${tick.symbol},${tick.price},${tick.changePercentage}"
    }
}

private fun Double.roundTo(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}