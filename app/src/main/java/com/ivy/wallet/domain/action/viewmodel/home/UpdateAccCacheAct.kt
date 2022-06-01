package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject

class UpdateAccCacheAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<List<Account>, List<Account>>() {
    override suspend fun List<Account>.compose(): suspend () -> List<Account> = suspend {
        val accounts = this

        ivyWalletCtx.updateCache { cache ->
            cache.copy(
                accounts = accounts,
                accountMap = accounts.associateBy { it.id }
            )
        }
        accounts
    }
}