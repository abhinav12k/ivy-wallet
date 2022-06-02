package com.ivy.wallet.domain.data

import arrow.core.Option
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Settings
import com.ivy.wallet.domain.pure.exchange.ExchangeRates
import java.util.*

data class IvyWalletCache(
    val settings: Option<Settings>,
    val baseCurrency: String,

    val accounts: List<Account>,
    val accountMap: Map<UUID, Account>,

    val categories: List<Category>,
    val categoryMap: Map<UUID, Category>,

    val startDayOfMonth: Int,

    val exchangeRates: Option<ExchangeRates>,
)