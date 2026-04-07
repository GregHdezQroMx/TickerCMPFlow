package presentation.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.StockTick
import domain.usecase.GetStockMetadataUseCase
import domain.usecase.GetStockUpdatesUseCase
import domain.usecase.ObserveConnectionStatusUseCase
import domain.usecase.ToggleStockTrackingUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI-Stable wrapper for the list of stocks.
 */
@Immutable
data class StockListState(
    val items: List<StockItemState> = emptyList()
)

/**
 * Data class to combine Tick data with Metadata for the UI.
 */
@Stable
data class StockItemState(
    val tick: StockTick,
    val companyName: String
)

/**
 * ViewModel for the Feed Screen.
 */
class StockViewModel(
    private val getStockUpdatesUseCase: GetStockUpdatesUseCase,
    private val toggleStockTrackingUseCase: ToggleStockTrackingUseCase,
    private val observeConnectionStatusUseCase: ObserveConnectionStatusUseCase,
    private val getStockMetadataUseCase: GetStockMetadataUseCase
) : ViewModel() {

    private val tag = this::class.simpleName ?: "StockViewModel"

    // Consuming global ThemeManager
    val isDarkTheme = ThemeManager.isDarkTheme

    val isConnected: StateFlow<Boolean> = observeConnectionStatusUseCase()
    val isTracking: StateFlow<Boolean> = toggleStockTrackingUseCase.isTrackingEnabled
    val isReconnecting: StateFlow<Boolean> = toggleStockTrackingUseCase.isReconnecting
    val isPersistentError: StateFlow<Boolean> = toggleStockTrackingUseCase.isPersistentError

    val stockListState: StateFlow<StockListState> = getStockUpdatesUseCase()
        .map { ticks ->
            val items = ticks.map { tick ->
                StockItemState(
                    tick = tick,
                    companyName = getStockMetadataUseCase(tick.symbol).name
                )
            }
            StockListState(items)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StockListState()
        )

    fun toggleTracking() {
        viewModelScope.launch {
            toggleStockTrackingUseCase()
        }
    }

    fun toggleTheme() {
        ThemeManager.toggleTheme()
    }

    fun onResume() {
        viewModelScope.launch {
            Napier.d(tag = tag) { "🚀 App Resumed: Syncing WebSocket state..." }
            toggleStockTrackingUseCase.resumeIfEnabled()
        }
    }

    fun onStop() {
        viewModelScope.launch {
            Napier.w(tag = tag) { "🔋 App Backgrounded: Pausing WebSocket physically" }
            toggleStockTrackingUseCase.pausePhysically()
        }
    }
}
