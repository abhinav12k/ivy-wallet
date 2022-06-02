package com.ivy.wallet.domain.pure.exchange

import arrow.core.Option
import com.ivy.frp.Pure
import com.ivy.wallet.domain.data.core.Transaction


data class ExchangeTrnArgument(
    val baseCurrency: String,
    val rates: ExchangeRates
)

@Pure
suspend fun exchangeInBaseCurrency(
    transaction: Transaction,
    arg: ExchangeTrnArgument
): Option<Double> = arg.rates.exchangeInBaseCurrency(
    transaction = transaction,
    baseCurrency = arg.baseCurrency,
)

@Pure
suspend fun ExchangeRates.exchangeInBaseCurrency(
    transaction: Transaction,
    baseCurrency: String,
): Option<Double> = exchangeInCurrency(
    transaction = transaction,
    baseCurrency = baseCurrency,
    toCurrency = baseCurrency,
)

@Pure
suspend fun ExchangeRates.exchangeInCurrency(
    baseCurrency: String,
    transaction: Transaction,
    toCurrency: String,
): Option<Double> = exchange(
    data = ExchangeData(
        baseCurrency = baseCurrency,
        fromCurrency = transaction.account.currency ?: baseCurrency,
        toCurrency = toCurrency
    ),
    amount = transaction.amount,
)