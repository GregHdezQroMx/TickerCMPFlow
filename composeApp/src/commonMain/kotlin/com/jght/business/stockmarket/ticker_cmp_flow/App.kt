package com.jght.business.stockmarket.ticker_cmp_flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.koin.compose.viewmodel.koinViewModel
import presentation.navigation.AppNavigation
import presentation.theme.TickerCMPFlowTheme
import presentation.viewmodel.StockViewModel

@Composable
fun App() {
    val viewModel = koinViewModel<StockViewModel>()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    TickerCMPFlowTheme(darkTheme = isDarkTheme) {
        
        // GLOBAL LIFECYCLE OBSERVER
        // This ensures that the Tracking Engine (UseCase Singleton)
        // is always aware of the App's physical state, solving the 
        // Deep Link from Background inconsistency.
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.onResume()
        }

        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            viewModel.onStop()
        }

        LaunchedEffect(Unit) {
            viewModel.toggleTracking()
        }

        AppNavigation()
    }
}
