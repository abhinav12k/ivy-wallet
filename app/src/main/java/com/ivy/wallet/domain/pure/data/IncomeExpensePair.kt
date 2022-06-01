package com.ivy.wallet.domain.pure.data



data class IncomeExpensePair(
    val income: Double,
    val expense: Double
) {
    companion object {
        fun zero(): IncomeExpensePair = IncomeExpensePair(0.0, 0.0)
    }
}