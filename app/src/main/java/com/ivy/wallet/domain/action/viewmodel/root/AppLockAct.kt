package com.ivy.wallet.domain.action.viewmodel.root

import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.SharedPrefs
import javax.inject.Inject

class AppLockAct @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = {
        sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false)
    }
}