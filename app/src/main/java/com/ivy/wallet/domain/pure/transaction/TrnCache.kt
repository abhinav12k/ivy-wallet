package com.ivy.wallet.domain.pure.transaction

import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import java.util.*

fun category(
    categoryId: UUID?,
    categoryMap: Map<UUID, Category>,
    categories: List<Category>
): Category? {
    val targetId = categoryId ?: return null
    return categoryMap[targetId] ?: categories.find { it.id == targetId }
}

fun account(
    accountId: UUID?,
    accountMap: Map<UUID, Account>,
    accounts: List<Account>
): Account? {
    val targetId = accountId ?: return null
    return accountMap[targetId] ?: accounts.find { it.id == targetId }
}