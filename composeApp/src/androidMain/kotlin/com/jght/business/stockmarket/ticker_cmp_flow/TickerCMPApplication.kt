package com.jght.business.stockmarket.ticker_cmp_flow

import android.app.Application
import com.jght.business.stockmarket.ticker_cmp_flow.di.appModule
import di.KoinInitializer

class TickerCMPApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin and Napier using the multiplatform initializer
        KoinInitializer(applicationContext).init(listOf(appModule))
    }
}