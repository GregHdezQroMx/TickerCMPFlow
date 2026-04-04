package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.StockTick
import domain.usecase.GetStockUpdatesUseCase
import domain.usecase.ObserveConnectionStatusUseCase
import domain.usecase.ToggleStockTrackingUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Feed Screen.
 * Pure orchestration of UseCases for a scalable Clean Architecture.
 */
class StockViewModel(
    private val getStockUpdatesUseCase: GetStockUpdatesUseCase,
    private val toggleStockTrackingUseCase: ToggleStockTrackingUseCase,
    private val observeConnectionStatusUseCase: ObserveConnectionStatusUseCase
) : ViewModel() {

    private val tag = this::class.simpleName ?: "StockViewModel"

    /**
     * Connection status (🟢 Connected / 🔴 Disconnected).
     */
    val isConnected: StateFlow<Boolean> = observeConnectionStatusUseCase()

    /**
     * Whether the price feed is currently active according to user intent.
     */
    val isTracking: StateFlow<Boolean> = toggleStockTrackingUseCase.isTracking

    /**
     * The stream of stock ticks, sorted by price (highest first).
     */
    val stockTicks: StateFlow<List<StockTick>> = getStockUpdatesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * User-triggered action to toggle the feed.
     */
    fun toggleTracking() {
        viewModelScope.launch {
            toggleStockTrackingUseCase()
        }
    }

    /**
     * Lifecycle-aware resume: Restores connection if user intent is 'tracking'.
     */
    fun onResume() {
        viewModelScope.launch {
            if (isTracking.value) {
                Napier.d(tag = tag) { "🚀 App Resumed: Reconnecting WebSocket to save resources" }
                toggleStockTrackingUseCase.startTracking()
            }
        }
    }

    /**
     * Lifecycle-aware stop: Physically closes connection to avoid battery drain.
     */
    fun onStop() {
        if (isTracking.value) {
            Napier.w(tag = tag) { "🔋 App Backgrounded: Disconnecting WebSocket to prevent Battery Drain" }
            toggleStockTrackingUseCase.stopTracking()
        }
    }
}
