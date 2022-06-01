package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.frp.SideEffect
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction

import java.util.*

typealias CategoryValueFunction = SuspendValueFunction<CategoryValueFunctions.Argument>

object CategoryValueFunctions {
    data class Argument(
        val categoryId: UUID?,
        val accounts: List<Account>,

        @SideEffect
        val exchangeToBaseCurrency: suspend (
            fromCurrency: Option<String>,
            amount: Double
        ) -> Option<Double>
    )

    suspend fun balance(
        transaction: Transaction,
        arg: Argument,
    ): Double = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                TransactionType.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId).negate()
                TransactionType.TRANSFER -> 0.0
            }
        } else 0.0
    }

    suspend fun income(
        transaction: Transaction,
        arg: Argument,
    ): Double = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> 0.0
            }
        } else 0.0
    }

    suspend fun expense(
        transaction: Transaction,
        arg: Argument,
    ): Double = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> 0.0
            }
        } else 0.0
    }

    suspend fun incomeCount(
        transaction: Transaction,
        arg: Argument,
    ): Double = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> Double.ONE
                else -> 0.0
            }
        } else 0.0
    }

    suspend fun expenseCount(
        transaction: Transaction,
        arg: Argument,
    ): Double = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.EXPENSE -> Double.ONE
                else -> 0.0
            }
        } else 0.0
    }

    private suspend fun Double.toBaseCurrencyOrZero(
        arg: Argument,
        accountId: UUID
    ): Double {
        return this.convertToBaseCurrency(
            arg = arg,
            accountId = accountId
        ).orNull() ?: 0.0
    }

    private suspend fun Double.convertToBaseCurrency(
        accountId: UUID,
        arg: Argument
    ): Option<Double> {
        val trnCurrency = arg.accounts.find { it.id == accountId }?.currency.toOption()
        return arg.exchangeToBaseCurrency(trnCurrency, this)
    }
}