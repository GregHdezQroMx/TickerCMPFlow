package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.StockTick
import domain.usecase.GetStockUpdatesUseCase
import domain.usecase.ObserveConnectionStatusUseCase
import domain.usecase.ToggleStockTrackingUseCase
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

    /**
     * Connection status (🟢 Connected / 🔴 Disconnected).
     */
    val isConnected: StateFlow<Boolean> = observeConnectionStatusUseCase()

    /**
     * Whether the price feed is currently active.
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
     * Starts or stops the price feed logic via UseCase.
     */
    fun toggleTracking() {
        viewModelScope.launch {
            toggleStockTrackingUseCase()
        }
    }
}