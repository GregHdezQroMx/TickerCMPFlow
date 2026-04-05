package presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.Candle
import domain.model.StockMetadata
import domain.model.StockTick
import domain.usecase.GetStockDetailUseCase
import domain.usecase.GetStockMetadataUseCase
import domain.usecase.ObserveConnectionStatusUseCase
import domain.usecase.ToggleStockTrackingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for the Symbol Details Screen.
 * Manages live connection status, metadata, and dynamic candlestick history.
 */
class StockDetailViewModel(
    private val symbol: String,
    private val getStockDetailUseCase: GetStockDetailUseCase,
    private val getStockMetadataUseCase: GetStockMetadataUseCase,
    private val toggleStockTrackingUseCase: ToggleStockTrackingUseCase,
    private val observeConnectionStatusUseCase: ObserveConnectionStatusUseCase
) : ViewModel() {

    /**
     * Physical connection status (🟢 Connected / 🔴 Disconnected).
     */
    val isConnected: StateFlow<Boolean> = observeConnectionStatusUseCase()

    /**
     * User's persistent tracking intent.
     */
    val isTracking: StateFlow<Boolean> = toggleStockTrackingUseCase.isTrackingEnabled

    /**
     * Active reconnection attempts (🟠 Reconnecting).
     */
    val isReconnecting: StateFlow<Boolean> = toggleStockTrackingUseCase.isReconnecting

    /**
     * Persistent error detection (🚨 Persistent Issue).
     */
    val isPersistentError: StateFlow<Boolean> = toggleStockTrackingUseCase.isPersistentError

    val metadata: StockMetadata = getStockMetadataUseCase(symbol)

    private val _history = MutableStateFlow<List<Candle>>(emptyList())
    val history: StateFlow<List<Candle>> = _history.asStateFlow()

    init {
        generateInitialMockHistory()
    }

    val stockTick: StateFlow<StockTick?> = getStockDetailUseCase(symbol)
        .onEach { tick -> tick?.let { updateHistoryWithTick(it) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun toggleTracking() {
        viewModelScope.launch {
            toggleStockTrackingUseCase()
        }
    }

    private fun generateInitialMockHistory() {
        val candles = mutableListOf<Candle>()
        var lastClose = Random.nextDouble(100.0, 500.0)
        repeat(20) {
            val open = lastClose
            val close = open * (1 + Random.nextDouble(-0.01, 0.01))
            candles.add(Candle(open, maxOf(open, close) * 1.005, minOf(open, close) * 0.995, close))
            lastClose = close
        }
        _history.value = candles
    }

    private fun updateHistoryWithTick(tick: StockTick) {
        val currentHistory = _history.value.toMutableList()
        val lastCandle = currentHistory.lastOrNull()
        
        val newCandle = if (lastCandle != null) {
            Candle(
                open = lastCandle.close,
                high = maxOf(lastCandle.close, tick.price) * 1.002,
                low = minOf(lastCandle.close, tick.price) * 0.998,
                close = tick.price
            )
        } else {
            Candle(tick.price, tick.price * 1.002, tick.price * 0.998, tick.price)
        }

        currentHistory.add(newCandle)
        if (currentHistory.size > 30) currentHistory.removeAt(0)
        _history.value = currentHistory
    }
}
