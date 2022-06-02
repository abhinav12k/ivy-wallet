package com.ivy.wallet.domain.action.exchange

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.exchange.exchange
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject

class ExchangeAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<ExchangeAct.Input, Option<Double>>() {
    override suspend fun Input.compose(): suspend () -> Option<Double> = suspend {
        when (val rates = ivyWalletCtx.cache.unsafeRead().exchangeRates) {
            is Some -> rates.value.exchange(
                data = data,
                amount = amount,
            )
            is None -> None
        }
    }

    data class Input(
        val data: ExchangeData,
        val amount: Double
    )
}

fun actInput(
    data: ExchangeData,
    amount: Double
): ExchangeAct.Input = ExchangeAct.Input(
    data = data,
    amount = amount
)
