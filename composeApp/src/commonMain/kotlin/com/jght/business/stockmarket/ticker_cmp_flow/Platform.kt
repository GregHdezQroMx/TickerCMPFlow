package com.jght.business.stockmarket.ticker_cmp_flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform