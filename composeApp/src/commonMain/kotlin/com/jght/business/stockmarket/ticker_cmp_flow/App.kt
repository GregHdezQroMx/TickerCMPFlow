package com.jght.business.stockmarket.ticker_cmp_flow

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import presentation.theme.TickerCMPFlowTheme
import tickercmpflow.shared.generated.resources.Res
import tickercmpflow.shared.generated.resources.symbols_list
import presentation.viewmodel.StockViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun App() {
    // 1. Aplicamos nuestro Design System OLED (Fintech Style)
    TickerCMPFlowTheme {
        
        // 2. Obtenemos los símbolos desde los recursos compartidos
        val symbolsRaw = stringResource(Res.string.symbols_list)
        val symbols = remember(symbolsRaw) { symbolsRaw.split(",").map { it.trim() } }

        // 3. Inyectamos el ViewModel de forma limpia (Sin parámetros manuales)
        val viewModel = koinViewModel<StockViewModel>()

        // 4. Iniciamos el tracking automáticamente al abrir la App (Requerimiento)
        LaunchedEffect(Unit) {
            viewModel.toggleTracking(symbols)
        }

        // TODO: Implementar NavHost con Navigation 2.8.x aquí
        // Por ahora, solo tenemos la estructura base lista.
    }
}