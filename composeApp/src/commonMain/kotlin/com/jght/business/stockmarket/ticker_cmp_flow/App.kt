package com.jght.business.stockmarket.ticker_cmp_flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.koin.compose.viewmodel.koinViewModel
import presentation.navigation.AppNavigation
import presentation.theme.TickerCMPFlowTheme
import presentation.viewmodel.StockViewModel

@Composable
fun App() {
    // 1. Aplicamos nuestro Design System OLED (Fintech Style)
    TickerCMPFlowTheme {
        
        // 2. Inyectamos el ViewModel de forma limpia (Singleton compartido)
        val viewModel = koinViewModel<StockViewModel>()

        // 3. Gestión de Ciclo de Vida (Opción B: Ahorro de Energía)
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.onResume()
        }

        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            viewModel.onStop()
        }

        // 4. Iniciamos el tracking automáticamente al abrir la App
        LaunchedEffect(Unit) {
            viewModel.toggleTracking()
        }

        // 5. Motor de Navegación con Shared Elements
        AppNavigation()
    }
}