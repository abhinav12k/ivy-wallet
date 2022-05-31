package com.ivy.wallet.domain.data

import arrow.core.Option
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Settings
import java.util.*

data class IvyWalletCache(
    val accounts: List<Account>,
    val accountMap: Map<UUID, Account>,
    val categories: List<Category>,
    val categoryMap: Map<UUID, Category>,
    val rates: Option<Map<String, Double>>,
    val settings: Option<Settings>,

    //start day of month is in IvyWalletCtx
)