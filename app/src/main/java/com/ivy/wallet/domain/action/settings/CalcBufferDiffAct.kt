package com.ivy.wallet.domain.action.settings

import com.ivy.frp.action.FPAction

import javax.inject.Inject

class CalcBufferDiffAct @Inject constructor() : FPAction<CalcBufferDiffAct.Input, Double>() {

    override suspend fun Input.compose(): suspend () -> Double = {
        balance - buffer
    }

    data class Input(
        val balance: Double,
        val buffer: Double
    )
}