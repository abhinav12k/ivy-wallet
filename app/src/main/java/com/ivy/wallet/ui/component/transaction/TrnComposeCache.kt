package com.ivy.wallet.ui.component.transaction

import androidx.compose.runtime.Composable
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.ui.ivyWalletCtx
import java.util.*

@Composable
fun category(
    categoryId: UUID?,
    categories: List<Category>
): Category? = com.ivy.wallet.domain.pure.transaction.category(
    categoryId = categoryId,
    categories = categories,
    categoryMap = ivyWalletCtx().categoryMap
)

@Composable
fun account(
    accountId: UUID?,
    accounts: List<Account>
): Account? = com.ivy.wallet.domain.pure.transaction.account(
    accountId = accountId,
    accounts = accounts,
    accountMap = ivyWalletCtx().accountMap
)