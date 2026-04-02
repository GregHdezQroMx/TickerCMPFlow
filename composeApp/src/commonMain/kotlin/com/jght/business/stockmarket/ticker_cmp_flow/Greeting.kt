package com.jght.business.stockmarket.ticker_cmp_flow

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}