package com.jght.business.stockmarket.ticker_cmp_flow.data.repository

import data.mapper.toStockTicks
import domain.model.StockTick
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.random.Random

private const val TAG = "StockRepo"
private const val PUSH_DELAY_MS = 2000L

class StockRepositoryImpl(
    private val client: HttpClient,
    private val json: Json,
    private val configProvider: ConfigProvider
) : StockRepository {

    private var session: DefaultClientWebSocketSession? = null
    private var trackingJob: Job? = null
    
    private val _stockTicks = MutableStateFlow<List<StockTick>>(emptyList())
    private val _isConnected = MutableStateFlow(false)
    private val _isTracking = MutableStateFlow(false)

    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    override val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    override fun observeStockUpdates(): Flow<List<StockTick>> = _stockTicks.asStateFlow()

    override suspend fun startTracking(symbols: List<String>) {
        if (_isTracking.value) return
        _isTracking.value = true
        
        // Clean Architecture: Fetch config from provider, not directly from resources
        val host = configProvider.getHost()
        val path = configProvider.getPath()

        trackingJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.wss(host = host, path = path) {
                    session = this
                    _isConnected.value = true
                    val receiverJob = launch { receiveEcho() }

                    while (isActive && _isTracking.value) {
                        val mockData = symbols.joinToString("|") { symbol ->
                            val price = Random.nextDouble(100.0, 500.0)
                            val change = Random.nextDouble(-5.0, 5.0)
                            "$symbol,$price,$change"
                        }
                        send(Frame.Text(mockData))
                        delay(PUSH_DELAY_MS)
                    }
                    receiverJob.cancel()
                }
            } catch (e: Exception) {
                Napier.e(tag = TAG) { "WSS Error: ${e.message}" }
            } finally {
                _isConnected.value = false
                session = null
            }
        }
    }

    override fun stopTracking() {
        _isTracking.value = false
        trackingJob?.cancel()
        trackingJob = null
    }

    private suspend fun receiveEcho() {
        try {
            session?.incoming?.receiveAsFlow()?.collect { frame ->
                if (frame is Frame.Text) {
                    val rawText = frame.readText()
                    val ticks = rawText.toStockTicks()

                    _stockTicks.update { ticks }

                    ticks.forEach { tick ->
                        Napier.v(tag = TAG) {
                            "📈 TICK -> Symbol: ${tick.symbol} | Price: ${tick.price}"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "Receiver Closed: ${e::class.simpleName} - ${e.message}" }
        }
    }
}