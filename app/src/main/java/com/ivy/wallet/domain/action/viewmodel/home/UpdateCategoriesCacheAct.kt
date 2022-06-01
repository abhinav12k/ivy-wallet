package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.ui.IvyWalletCtx
import javax.inject.Inject

class UpdateCategoriesCacheAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<List<Category>, List<Category>>() {
    override suspend fun List<Category>.compose(): suspend () -> List<Category> = suspend {
        val categories = this

        ivyWalletCtx.updateCache { cache ->
            cache.copy(
                categories = categories,
                categoryMap = categories.associateBy { it.id }
            )
        }

        categories
    }
}