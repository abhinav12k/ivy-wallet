package com.ivy.wallet.ui.data

import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category

import java.time.LocalDateTime
import java.util.*

data class DisplayTransaction(
    val account: Account?,
    val type: TransactionType,
    val amount: Double,
    val toAccount: Account?,
    val toAmount: Double,
    val title: String?,
    val description: String?,
    val dateTime: LocalDateTime?,
    val category: Category?,
    val dueDate: LocalDateTime?,

    val recurringRuleId: UUID?,

    val attachmentUrl: String?,

    //This refers to the loan id that is linked with a transaction
    val loanId: UUID? = null,

    //This refers to the loan record id that is linked with a transaction
    val loanRecordId: UUID? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,


    val id: UUID = UUID.randomUUID()
) : TransactionHistoryItem