package data.repository

import data.mapper.toStockTicks
import data.mapper.toWireFormat
import domain.model.StockTick
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val TAG = "StockRepo"

class StockRepositoryImpl(
    private val client: HttpClient,
    private val json: Json,
    private val configProvider: ConfigProvider
) : StockRepository {

    private var session: DefaultClientWebSocketSession? = null
    private var connectionJob: Job? = null
    
    private val _stockTicks = MutableStateFlow<List<StockTick>>(emptyList())
    private val _isConnected = MutableStateFlow(false)

    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    override fun observeStockUpdates(): Flow<List<StockTick>> = _stockTicks.asStateFlow()

    override suspend fun connect() {
        if (_isConnected.value) return
        
        val host = configProvider.getHost()
        val path = configProvider.getPath()

        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.wss(host = host, path = path) {
                    session = this
                    _isConnected.value = true
                    receiveEcho()
                }
            } catch (e: Exception) {
                Napier.e(tag = TAG) { "WSS Connection Error: ${e.message}" }
            } finally {
                _isConnected.value = false
                session = null
            }
        }
    }

    override fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        _isConnected.value = false
    }

    override suspend fun sendTicks(ticks: List<StockTick>) {
        try {
            val rawData = ticks.toWireFormat()
            session?.send(Frame.Text(rawData))
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "Failed to send ticks: ${e.message}" }
        }
    }

    private suspend fun receiveEcho() {
        try {
            session?.incoming?.receiveAsFlow()?.collect { frame ->
                if (frame is Frame.Text) {
                    val rawText = frame.readText()
                    val ticks = rawText.toStockTicks()
                    _stockTicks.update { ticks }
                }
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "Receiver Error: ${e.message}" }
        }
    }
}