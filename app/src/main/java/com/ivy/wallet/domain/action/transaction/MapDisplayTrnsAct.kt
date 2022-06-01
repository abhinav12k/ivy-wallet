package com.ivy.wallet.domain.action.transaction

import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.lambda
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.transaction.account
import com.ivy.wallet.domain.pure.transaction.category
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.data.DisplayTransaction
import javax.inject.Inject

class MapDisplayTrnsAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<MapDisplayTrnsAct.Input, List<DisplayTransaction>>() {
    override suspend fun Input.compose(): suspend () -> List<DisplayTransaction> =
        transactions.lambda() thenMap {
            DisplayTransaction(
                account = account(
                    accountId = it.account.id,
                    accountMap = ivyWalletCtx.cache.accountMap,
                    accounts = accounts
                ),
                type = it.type,
                amount = it.amount,
                toAccount = account(
                    accountId = it.toAccount?.id,
                    accountMap = ivyWalletCtx.accountMap,
                    accounts = accounts
                ),
                toAmount = it.amount,
                title = it.title,
                description = it.description,
                dateTime = it.dateTime,
                category = category(
                    categoryId = it.categoryId,
                    categories = categories,
                    categoryMap = ivyWalletCtx.categoryMap
                ),
                dueDate = it.dueDate,
                recurringRuleId = it.recurringRuleId,
                attachmentUrl = it.attachmentUrl,
                loanId = it.loanId,
                loanRecordId = it.loanRecordId,
                id = it.id
            )
        }

    data class Input(
        val accounts: List<Account>,
        val categories: List<Category>,
        val transactions: List<Transaction>
    )
}