package domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Represents a real-time price update for a specific asset.
 * Marked as @Immutable to optimize Compose recomposition during high-frequency updates.
 */
@Immutable
@Serializable
data class StockTick(
    val symbol: String,
    val price: Double,
    val changePercentage: Double,
    val timestamp: Long
)
