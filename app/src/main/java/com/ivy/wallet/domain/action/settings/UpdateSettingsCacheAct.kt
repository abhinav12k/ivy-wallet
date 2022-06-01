package com.ivy.wallet.domain.action.settings

import arrow.core.Some
import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.data.core.Settings
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject


class UpdateSettingsCacheAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<Settings, Unit>() {
    override suspend fun Settings.compose(): suspend () -> Unit = {
        ivyWalletCtx.updateCache {
            it.copy(
                settings = Some(this),
                baseCurrency = baseCurrency
            )
        }
    }
}