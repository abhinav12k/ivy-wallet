package com.ivy.wallet.domain.pure.transaction

import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction

import java.util.*

typealias AccountValueFunction = ValueFunction<UUID>

object AccountValueFunctions {
    fun balance(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.account.id == accountId) {
            //Account's transactions
            when (type) {
                TransactionType.INCOME -> amount
                TransactionType.EXPENSE -> amount.toBigDecimal().negate()
                TransactionType.TRANSFER -> {
                    if (toAccount?.id != accountId) {
                        //transfer to another account
                        amount.toBigDecimal().negate()
                    } else {
                        //transfer to self
                        toAmount.minus(amount)
                    }
                }
            }
        } else {
            //potential transfer to account?
            toAccount?.id?.takeIf { it == accountId } ?: return 0.0
            toAmount
        }
    }

    fun income(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.account.id == accountId && type == TransactionType.INCOME)
            amount else 0.0
    }

    fun transferIncome(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.toAccount.Id == accountId && type == TransactionType.TRANSFER)
            toAmount else 0.0
    }

    fun expense(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            amount else 0.0
    }

    fun transferExpense(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.TRANSFER)
            amount else 0.0
    }


    fun incomeCount(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.INCOME)
            Double.ONE else 0.0
    }

    fun expenseCount(
        transaction: Transaction,
        accountId: UUID
    ): Double = with(transaction) {
        if (this.accountId == accountId && type == TransactionType.EXPENSE)
            Double.ONE else 0.0
    }
}