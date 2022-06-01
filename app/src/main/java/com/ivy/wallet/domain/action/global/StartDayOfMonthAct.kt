package com.ivy.wallet.domain.action.global

import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.SharedPrefs
import javax.inject.Inject

class StartDayOfMonthAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
) : FPAction<Unit, Int>() {

    override suspend fun Unit.compose(): suspend () -> Int = suspend {
        sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
    }
}