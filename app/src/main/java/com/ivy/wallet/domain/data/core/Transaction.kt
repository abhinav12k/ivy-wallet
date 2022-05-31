package com.ivy.wallet.domain.data.core

import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.io.network.data.TransactionDTO
import com.ivy.wallet.io.persistence.data.TransactionEntity
import java.time.LocalDateTime
import java.util.*

data class Transaction(
    val account: Account,
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
    val loanId: UUID?,
    //This refers to the loan record id that is linked with a transaction
    val loanRecordId: UUID?,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
) : TransactionHistoryItem {
    fun toEntity(): TransactionEntity = TransactionEntity(
        accountId = account.id,
        type = type,
        amount = amount,
        toAccountId = toAccount?.id,
        toAmount = toAmount,
        title = title,
        description = description,
        dateTime = dateTime,
        categoryId = category?.id,
        dueDate = dueDate,
        recurringRuleId = recurringRuleId,
        attachmentUrl = attachmentUrl,
        loanId = loanId,
        loanRecordId = loanRecordId,
        id = id,
        isSynced = isSynced,
        isDeleted = isDeleted
    )

    fun toDTO(): TransactionDTO = TransactionDTO(
        accountId = account.id,
        type = type,
        amount = amount,
        toAccountId = toAccount?.id,
        toAmount = toAmount,
        title = title,
        description = description,
        dateTime = dateTime,
        categoryId = category?.id,
        dueDate = dueDate,
        recurringRuleId = recurringRuleId,
        attachmentUrl = attachmentUrl,
        loanId = loanId,
        loanRecordId = loanRecordId,
        id = id
    )
}