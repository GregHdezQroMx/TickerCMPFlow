package com.jght.business.stockmarket.ticker_cmp_flow.data.repository

import data.mapper.toStockTicks
import domain.model.StockTick
import domain.repository.StockRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.random.Random

class StockRepositoryImpl(
    private val client: HttpClient,
    private val json: Json,
    private val baseUrl: String,
    private val apiPath: String
) : StockRepository {

    private var session: DefaultClientWebSocketSession? = null
    private val _stockTicks = MutableStateFlow<List<StockTick>>(emptyList())

    override fun observeStockUpdates(): Flow<List<StockTick>> = _stockTicks.asStateFlow()

    override suspend fun pushSymbols(symbols: List<String>) {
        withContext(Dispatchers.IO) {
            try {
                client.wss(host = baseUrl, path = apiPath) {
                    session = this
                    val receiverJob = launch { receiveEcho() }

                    while (isActive) {
                        val mockData = symbols.joinToString("|") { symbol ->
                            val price = Random.nextDouble(100.0, 500.0)
                            val change = Random.nextDouble(-5.0, 5.0)
                            "$symbol,$price,$change"
                        }
                        send(Frame.Text(mockData))
                        delay(2000)
                    }
                    receiverJob.cancel()
                }
            } catch (e: Exception) {
                Napier.e(tag = "StockRepo") { "WSS Error: ${e.message}" }
            } finally {
                session = null
            }
        }
    }

    private suspend fun receiveEcho() {
        try {
            session?.incoming?.receiveAsFlow()?.collect { frame ->
                if (frame is Frame.Text) {
                    val rawText = frame.readText()
                    val ticks = rawText.toStockTicks()

                    _stockTicks.update { ticks }

                    ticks.forEach { tick ->
                        Napier.d(tag = "StockRepo") {
                            "📈 TICK -> Symbol: ${tick.symbol} | Price: ${tick.price} | %: ${tick.changePercentage} | TS: ${tick.timestamp}"
                        }
                    }

                    Napier.v(tag = "StockRepo") { "✅ Batch of ${ticks.size} processed and emitted to Flow" }
                }
            }
        } catch (e: Exception) {
            Napier.e(tag = "StockRepo") { "Receiver Closed: ${e.message}" }
        }
    }
}