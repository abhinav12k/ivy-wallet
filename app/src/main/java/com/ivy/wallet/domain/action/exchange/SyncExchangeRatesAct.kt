package com.ivy.wallet.domain.action.exchange

import arrow.core.Some
import com.ivy.frp.action.FPAction
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.thenIfSuccess
import com.ivy.frp.monad.tryOpWithParam
import com.ivy.wallet.domain.data.core.ExchangeRate
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.service.CoinbaseService
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject

class SyncExchangeRatesAct @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
    restClient: RestClient,
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<SyncExchangeRatesAct.Input, Res<Exception, Unit>>() {
    private val coinbaseService = restClient.coinbaseService

    override suspend fun Input.compose(): suspend () -> Res<Exception, Unit> = suspend {
        if (baseCurrency.isBlank()) Res.Err(IllegalArgumentException("baseCurrency is blank!"))
        else Res.Ok(baseCurrency)
    } thenIfSuccess tryOpWithParam { baseCurrency ->
        val response = coinbaseService.getExchangeRates(
            url = CoinbaseService.exchangeRatesUrl(
                baseCurrencyCode = baseCurrency
            )
        )

        val rates = response.data.rates.map { (currency, rate) ->
            ExchangeRate(
                baseCurrency = baseCurrency,
                currency = currency,
                rate = rate
            ).toEntity()
        }
        exchangeRateDao.saveAll(rates)

        ivyWalletCtx.updateCache { cache ->
            cache.copy(
                exchangeRates = Some(
                    rates.associate { it.currency to it.rate }
                )
            )
        }
    }

    data class Input(
        val baseCurrency: String
    )
}