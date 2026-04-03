package presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Feed : Route

    @Serializable
    data class Details(val symbol: String) : Route
}