package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.StockTick
import domain.repository.StockRepository
import domain.usecase.GetStockUpdatesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: StockRepository,
    private val getStockUpdatesUseCase: GetStockUpdatesUseCase
) : ViewModel() {

    /**
     * Connection status indicator.
     */
    val isConnected: StateFlow<Boolean> = repository.isConnected

    /**
     * Status of the price tracking feed.
     */
    val isTracking: StateFlow<Boolean> = repository.isTracking

    /**
     * Sorted real-time stock ticks.
     */
    val stockTicks: StateFlow<List<StockTick>> = getStockUpdatesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Controls the start/stop state of the WebSocket stream.
     */
    fun toggleTracking(symbols: List<String>) {
        viewModelScope.launch {
            if (isTracking.value) {
                repository.stopTracking()
            } else {
                repository.startTracking(symbols)
            }
        }
    }
}