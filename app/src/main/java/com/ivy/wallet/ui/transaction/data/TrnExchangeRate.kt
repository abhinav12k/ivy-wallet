package com.ivy.wallet.ui.transaction.data



data class TrnExchangeRate(
    val fromCurrency: String,
    val toCurrency: String,
    val rate: Double
)