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
import io.ktor.websocket.send
import kotlinx.coroutines.CompletableDeferred
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json

private const val TAG = "StockRepo"
private const val CONNECTION_TIMEOUT_MS = 5000L

class StockRepositoryImpl(
    private val client: HttpClient,
    private val json: Json,
    private val configProvider: ConfigProvider
) : StockRepository {

    private var session: DefaultClientWebSocketSession? = null
    private var connectionJob: Job? = null
    private val connectionMutex = Mutex()
    
    private val _stockTicks = MutableStateFlow<List<StockTick>>(emptyList())
    private val _isConnected = MutableStateFlow(false)

    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    override fun observeStockUpdates(): Flow<List<StockTick>> = _stockTicks.asStateFlow()

    override suspend fun connect(): Boolean = connectionMutex.withLock {
        if (_isConnected.value) return true
        
        val host = configProvider.getHost()
        val path = configProvider.getPath()
        val connectionResult = CompletableDeferred<Boolean>()

        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.wss(host = host, path = path) {
                    session = this
                    _isConnected.value = true
                    if (!connectionResult.isCompleted) connectionResult.complete(true)
                    Napier.d(tag = TAG) { "✅ WebSocket Connected" }
                    receiveEcho()
                }
            } catch (e: Exception) {
                Napier.e(tag = TAG) { "❌ WSS Error: ${e.message}" }
                if (!connectionResult.isCompleted) connectionResult.complete(false)
            } finally {
                _isConnected.value = false
                session = null
                Napier.w(tag = TAG) { "🔌 WebSocket Disconnected" }
            }
        }

        return try {
            withTimeout(CONNECTION_TIMEOUT_MS) {
                connectionResult.await()
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "⏳ Connection Timeout" }
            disconnect()
            false
        }
    }

    override fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        _isConnected.value = false
    }

    override suspend fun sendTicks(ticks: List<StockTick>) {
        try {
            if (_isConnected.value) {
                val rawData = ticks.toWireFormat()
                session?.send(Frame.Text(rawData))
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { "⚠️ Failed to send ticks: ${e.message}" }
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
            Napier.e(tag = TAG) { "📉 Receiver Error: ${e.message}" }
        }
    }
}
