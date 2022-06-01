package com.ivy.wallet.domain.action.global

import com.ivy.frp.action.FPAction
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject

class UpdateStartDayOfMonthCacheAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<Int, Int>() {
    override suspend fun Int.compose(): suspend () -> Int = {
        ivyWalletCtx.updateCache { it.copy(startDayOfMonth = this) }
        this
    }
}