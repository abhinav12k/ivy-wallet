package com.ivy.wallet.domain.pure.util

import arrow.core.NonEmptyList
import arrow.core.Option


fun <T> NonEmptyList<T>.mapIndexedNel(
    f: (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed(f)
    )
}

suspend fun <T> NonEmptyList<T>.mapIndexedNelSuspend(
    f: suspend (Int, T) -> T
): NonEmptyList<T> {
    return NonEmptyList.fromListUnsafe(
        this.mapIndexed { index, value ->
            f(index, value)
        }
    )
}

fun nonEmptyListOfZeros(n: Int): NonEmptyList<Double> {
    return NonEmptyList.fromListUnsafe(
        List(n) { 0.0 }
    )
}

fun Option<Double>.orZero(): Double {
    return this.orNull() ?: 0.0
}