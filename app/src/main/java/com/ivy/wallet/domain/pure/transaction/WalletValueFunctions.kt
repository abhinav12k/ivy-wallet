package com.ivy.wallet.domain.pure.transaction

import com.ivy.frp.SideEffect
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.exchange.ExchangeEffect
import com.ivy.wallet.domain.pure.exchange.exchangeInBaseCurrency


object WalletValueFunctions {
    data class Argument(
        val accounts: List<Account>,
        val baseCurrency: String,

        @SideEffect
        val exchange: ExchangeEffect
    )

    suspend fun income(
        transaction: Transaction,
        arg: Argument
    ): Double = with(transaction) {
        when (type) {
            TransactionType.INCOME -> exchangeInBaseCurrency(
                transaction = this,
                accounts = arg.accounts,
                baseCurrency = arg.baseCurrency,
                exchange = arg.exchange
            )
            else -> 0.0
        }
    }

    suspend fun transferIncome(
        transaction: Transaction,
        arg: Argument
    ): Double = with(transaction) {
        val condition = arg.accounts.any { it.id == this.toAccountId }
        when {
            type == TransactionType.TRANSFER && condition ->
                exchangeInBaseCurrency(
                    transaction = this.copy(
                        amount = this.toAmount,
                        accountId = this.toAccountId ?: this.accountId
                    ), //Do not remove copy()
                    accounts = arg.accounts,
                    baseCurrency = arg.baseCurrency,
                    exchange = arg.exchange
                )
            else -> 0.0
        }
    }

    suspend fun expense(
        transaction: Transaction,
        arg: Argument
    ): Double = with(transaction) {
        when (type) {
            TransactionType.EXPENSE -> exchangeInBaseCurrency(
                transaction = this,
                accounts = arg.accounts,
                baseCurrency = arg.baseCurrency,
                exchange = arg.exchange
            )
            else -> 0.0
        }
    }

    suspend fun transferExpenses(
        transaction: Transaction,
        arg: Argument
    ): Double = with(transaction) {
        val condition = arg.accounts.any { it.id == this.accountId }
        when {
            type == TransactionType.TRANSFER && condition -> exchangeInBaseCurrency(
                transaction = this,
                accounts = arg.accounts,
                baseCurrency = arg.baseCurrency,
                exchange = arg.exchange
            )
            else -> 0.0
        }
    }
}