package com.ivy.wallet.domain.pure.data



data class IncomeExpenseTransferPair(
    val income: Double,
    val expense: Double,
    val transferIncome: Double,
    val transferExpense: Double
) {
    companion object {
        fun zero(): IncomeExpenseTransferPair = IncomeExpenseTransferPair(
            0.0,
            0.0,
            0.0,
            0.0
        )
    }
}
