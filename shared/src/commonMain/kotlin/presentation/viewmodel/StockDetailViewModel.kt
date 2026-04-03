package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.StockMetadata
import domain.model.StockTick
import domain.usecase.GetStockDetailUseCase
import domain.usecase.GetStockMetadataUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Symbol Details Screen.
 * Provides real-time updates for a single symbol and its metadata.
 */
class StockDetailViewModel(
    private val symbol: String,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val getStockMetadataUseCase: GetStockMetadataUseCase
) : ViewModel() {

    /**
     * Static information about the company.
     */
    val metadata: StockMetadata = getStockMetadataUseCase(symbol)

    /**
     * Real-time price updates for this specific symbol.
     */
    val stockTick: StateFlow<StockTick?> = getStockDetailUseCase(symbol)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
