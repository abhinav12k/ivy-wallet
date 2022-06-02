package com.ivy.wallet.domain.pure.exchange

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.computations.option
import com.ivy.frp.Pure
import com.ivy.wallet.utils.isNotNullOrBlank

typealias ExchangeRates = Map<String, Double>

data class ExchangeData(
    val baseCurrency: String,
    val fromCurrency: String,
    val toCurrency: String = baseCurrency,
)

@Pure
suspend fun ExchangeRates.exchange(
    data: ExchangeData,
    amount: Double,
): Option<Double> = option {
    if (amount == 0.0) {
        return@option 0.0
    }

    val fromCurrency = data.fromCurrency.validateCurrency().bind()
    val toCurrency = data.toCurrency.validateCurrency().bind()

    if (fromCurrency == toCurrency) {
        return@option amount
    }

    when (data.baseCurrency.validateCurrency().bind()) {
        fromCurrency -> {
            //exchange from base currency to other currency
            //we need the rate from baseCurrency to toCurrency
            val rateFromTo = getValidExchangeRate(toCurrency).bind()

            //toAmount = fromAmount * rateFromTo
            amount * rateFromTo
        }
        toCurrency -> {
            //exchange from other currency to base currency
            //we'll get the rate to

            val rateToFrom = getValidExchangeRate(fromCurrency).bind()

            /*
            Example: fromA = 10 fromC = EUR; toC = BGN
            rateToFrom = rate (BGN EUR) ~= 0.51

            Formula: (10 EUR / 0.51 ~= 19.67)
                fromAmount / rateToFrom

            EXPECTED: 10 EUR ~= 19.67 BGN
             */
            amount / rateToFrom
        }
        else -> {
            //exchange from other currency to other currency
            //that's the only possible case left because we already checked "fromCurrency == toCurrency"

            val rateBaseFrom = getValidExchangeRate(fromCurrency).bind()
            val rateBaseTo = getValidExchangeRate(toCurrency).bind()

            //Convert: toBaseCurrency -> toToCurrency
            val amountBaseCurrency = amount / rateBaseFrom
            amountBaseCurrency * rateBaseTo
        }
    }
}

@Pure
private fun String.validateCurrency(): Option<String> {
    return if (this.isNotNullOrBlank()) return Some(this) else None
}

/**
 * Returns an exchange rate between "baseCurrency" and the selected "currency".
 */
@Pure
private fun ExchangeRates.getValidExchangeRate(
    currency: String,
): Option<Double> = this[currency].validateRate()

@Pure
private fun Double?.validateRate(): Option<Double> {
    //exchange rate which <= 0 is invalid!
    return if (this != null && this > 0) return Some(this) else None
}