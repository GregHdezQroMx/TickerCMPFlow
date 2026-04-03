package com.jght.business.stockmarket.ticker_cmp_flow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.repository.StockRepository
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import tickercmpflow.shared.generated.resources.Res
import tickercmpflow.shared.generated.resources.symbols_list
import tickercmpflow.shared.generated.resources.ws_host
import tickercmpflow.shared.generated.resources.ws_path

@Composable
fun App() {
    val host = stringResource(Res.string.ws_host)
    val path = stringResource(Res.string.ws_path)
    val symbolsRaw = stringResource(Res.string.symbols_list)
    val symbols = remember(symbolsRaw) { symbolsRaw.split(",") }

    // Injecting the Repository from the :shared module (ANTI-PATTERN) BUT WILL BE REMOVED IN THE FUTURE
    val repository = koinInject<StockRepository> { parametersOf(host, path) }

    LaunchedEffect(Unit) {
        // This starts the 2-second interval Echo in the background
        repository.pushSymbols(symbols)
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Phase 1: WebSocket Active. Check Logcat.")
        }
    }
}