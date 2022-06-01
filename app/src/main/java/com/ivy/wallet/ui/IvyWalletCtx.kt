package com.ivy.wallet.ui

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import arrow.core.None
import arrow.fx.stm.TVar
import arrow.fx.stm.atomically
import com.ivy.design.IvyContext
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
import com.ivy.wallet.domain.data.IvyWalletCache
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.paywall.PaywallReason
import java.time.LocalDate
import java.time.LocalTime

class IvyWalletCtx : IvyContext() {
    //------------------------------------------ State ---------------------------------------------
    lateinit var cache: TVar<IvyWalletCache>

    suspend fun initCache() {
        cache = TVar.new(defaultCache())
    }

    private fun defaultCache(): IvyWalletCache = IvyWalletCache(
        accounts = emptyList(),
        accountMap = emptyMap(),
        categories = emptyList(),
        categoryMap = emptyMap(),
        exchangeRates = None,
        settings = None,
        baseCurrency = "",
        startDayOfMonth = 1
    )

    suspend fun updateCache(update: (IvyWalletCache) -> IvyWalletCache) {
        atomically {
            cache.write(update(cache.read()))
        }
    }


    var selectedPeriod: TimePeriod = TimePeriod.currentMonth(
        startDayOfMonth = 1 //this is default value
    )

    private var selectedPeriodInitialized = false
    fun initSelectedPeriodInMemory(
        startDayOfMonth: Int,
        forceReinitialize: Boolean = false
    ): TimePeriod {
        if (!selectedPeriodInitialized || forceReinitialize) {
            selectedPeriod = TimePeriod.currentMonth(
                startDayOfMonth = startDayOfMonth
            )
            selectedPeriodInitialized = true
        }

        return selectedPeriod
    }

    fun updateSelectedPeriodInMemory(period: TimePeriod) {
        selectedPeriod = period
    }


    var transactionsListState: LazyListState? = null

    var mainTab by mutableStateOf(MainTab.HOME)
        private set

    fun selectMainTab(tab: MainTab) {
        mainTab = tab
    }

    var moreMenuExpanded = false
        private set

    fun setMoreMenuExpanded(expanded: Boolean) {
        moreMenuExpanded = expanded
    }
    //------------------------------------------ State ---------------------------------------------

    //------------------------------------------- Navigation ----------------------------------------
    fun protectWithPaywall(
        paywallReason: PaywallReason,
        navigation: Navigation,
        action: () -> Unit
    ) {
        if (isPremium || (BuildConfig.DEBUG && !Constants.ENABLE_PAYWALL_ON_DEBUG)) {
            action()
        } else {
            navigation.navigateTo(
                screen = Paywall(
                    paywallReason = paywallReason
                )
            )
        }
    }
    //------------------------------------------- Navigation ----------------------------------------

    //Activity help -------------------------------------------------------------------------------
    lateinit var onShowDatePicker: (
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) -> Unit
    lateinit var onShowTimePicker: (onDatePicked: (LocalTime) -> Unit) -> Unit

    fun datePicker(
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) {
        onShowDatePicker(minDate, maxDate, initialDate, onDatePicked)
    }

    fun timePicker(onTimePicked: (LocalTime) -> Unit) {
        onShowTimePicker(onTimePicked)
    }
    //Activity help -------------------------------------------------------------------------------


    // Billing -------------------------------------------------------------------------------------
    var isPremium = true //if (BuildConfig.DEBUG) Constants.PREMIUM_INITIAL_VALUE_DEBUG else false
    // Billing -------------------------------------------------------------------------------------

    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    //Testing --------------------------------------------------------------------------------------
    suspend fun reset() {
        mainTab = MainTab.HOME
        atomically {
            cache.write(defaultCache())
        }
        isPremium = true
        transactionsListState = null
    }
}
