package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.StockTick
import domain.usecase.GetStockUpdatesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StockViewModel(
    getStockUpdatesUseCase: GetStockUpdatesUseCase
) : ViewModel() {

    // Convertimos el Flow del UseCase en un StateFlow para la UI
    // 'WhileSubscribed' asegura que el socket no gaste datos si la app está en background
    val stockTicks: StateFlow<List<StockTick>> = getStockUpdatesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}