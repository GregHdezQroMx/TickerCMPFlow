package com.jght.business.stockmarket.ticker_cmp_flow

import android.app.Application
import com.jght.business.stockmarket.ticker_cmp_flow.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TickerCMPApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Contexto de Android para Koin
            androidContext(this@TickerCMPApplication)
            // Carga el módulo que definiste en commonMain
            modules(appModule)
        }
    }
}