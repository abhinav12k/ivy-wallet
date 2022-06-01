package com.ivy.wallet.ui.root

import android.content.Intent
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.viewModelScope
import arrow.core.Some
import com.ivy.design.l0_system.Theme
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.viewmodel.FRPViewModel
import com.ivy.wallet.Constants
import com.ivy.wallet.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.global.LoadIvySessionAct
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.global.UpdateStartDayOfMonthCacheAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.settings.UpdateSettingsCacheAct
import com.ivy.wallet.domain.action.viewmodel.home.UpdateAccCacheAct
import com.ivy.wallet.domain.action.viewmodel.home.UpdateCategoriesCacheAct
import com.ivy.wallet.domain.action.viewmodel.root.AppLockAct
import com.ivy.wallet.domain.action.viewmodel.root.OnboardingCompletedAct
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.deprecated.logic.notification.TransactionReminderLogic
import com.ivy.wallet.domain.deprecated.sync.IvySync
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.EditTransaction
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.Onboarding
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class RootViewModel @Inject constructor(
    private val ivyContext: IvyWalletCtx,
    private val nav: Navigation,

    private val transactionReminderLogic: TransactionReminderLogic,
    private val ivySync: IvySync,

    private val settingsAct: SettingsAct,
    private val updateSettingsCacheAct: UpdateSettingsCacheAct,
    private val onboardingCompletedAct: OnboardingCompletedAct,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val updateAccCacheAct: UpdateAccCacheAct,
    private val updateCategoriesCacheAct: UpdateCategoriesCacheAct,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val updateStartDayOfMonthCacheAct: UpdateStartDayOfMonthCacheAct,
    private val appLockAct: AppLockAct,
    private val loadIvySessionAct: LoadIvySessionAct,
) : FRPViewModel<RootState, RootEvent>() {

    override val _state: MutableStateFlow<RootState> = MutableStateFlow(
        RootState(appLocked = null)
    )

    companion object {
        const val EXTRA_ADD_TRANSACTION_TYPE = "add_transaction_type_extra"
    }

    private var appLockEnabled = false


    override suspend fun handleEvent(event: RootEvent): suspend () -> RootState = when (event) {
        is RootEvent.Start -> start(event)
        RootEvent.LockApp -> ::lockApp
        RootEvent.UnlockApp -> ::unlockApp
        RootEvent.Internal.LoadTheme -> loadTheme()
    }

    private fun start(event: RootEvent.Start) = suspend {
        ivyContext.initCache()
        onEvent(RootEvent.Internal.LoadTheme) //load theme async
    } then onboardingCompletedAct then { onboarded ->
        if (onboarded) startForOnboarded(event) else startForNewUser()
        stateVal()
    }

    private fun startForOnboarded(event: RootEvent.Start) = suspend {
        viewModelScope.launch {
            awaitAll(
                async {
                    categoriesAct thenInvokeAfter updateCategoriesCacheAct
                },
                async {
                    accountsAct thenInvokeAfter updateAccCacheAct
                },
                async {
                    startDayOfMonthAct thenInvokeAfter updateStartDayOfMonthCacheAct
                },
                async {
                    settingsAct thenInvokeAfter updateSettingsCacheAct
                },
            )
        }
    } then {
        navigateOnboardedUser(event.intent) //navigate to HomeTab or to EditTransaction screen

        viewModelScope.launch {
            appLockAct thenInvokeAfter { appLocked ->
                appLockEnabled = appLocked
                updateState { it.copy(appLocked = appLocked) }
            }
        }

        viewModelScope.launch {
            delay(10.seconds)

            ioThread {
                //TODO: Refactor as action and defer because it's not important
                transactionReminderLogic.scheduleReminder()
            }
        }

        viewModelScope.launch {
            delay(5.seconds)

            loadIvySessionAct thenInvokeAfter {
                ivySync.sync()
            }
        }
    }

    private fun startForNewUser() {
        nav.navigateTo(Onboarding)
    }

    private fun loadTheme() = suspend {
        ivyContext.switchTheme(Theme.AUTO)
    } then settingsAct then { settings ->
        ivyContext.switchTheme(settings.theme) //update to the selected by the user theme

        ivyContext.updateCache { it.copy(settings = Some(settings)) }

        stateVal()
    }

    private fun navigateOnboardedUser(intent: Intent) {
        if (!handleSpecialStart(intent)) {
            nav.navigateTo(Main)
        }
    }

    private fun handleSpecialStart(intent: Intent): Boolean {
        val addTrnType: TransactionType? = try {
            intent.getSerializableExtra(EXTRA_ADD_TRANSACTION_TYPE) as? TransactionType
                ?: TransactionType.valueOf(intent.getStringExtra(EXTRA_ADD_TRANSACTION_TYPE) ?: "")
        } catch (e: IllegalArgumentException) {
            null
        }


        if (addTrnType != null) {
            nav.navigateTo(
                EditTransaction(
                    initialTransactionId = null,
                    type = addTrnType
                )
            )

            return true
        }

        return false
    }

    fun handleBiometricAuthResult(
        onAuthSuccess: () -> Unit = {}
    ): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Timber.d(stringRes(R.string.authentication_succeeded))
                unlockApp()
                onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                Timber.d(stringRes(R.string.authentication_failed))
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

            }
        }
    }

    //App Lock & UserInactivity --------------------------------------------------------------------
    fun isAppLockEnabled(): Boolean {
        return appLockEnabled
    }

    fun isAppLocked(): Boolean {
        //by default we assume that the app is locked
        return stateVal().appLocked ?: true
    }

    private fun lockApp() = updateStateNonBlocking { it.copy(appLocked = true) }

    private fun unlockApp() = updateStateNonBlocking { it.copy(appLocked = false) }

    private val userInactiveTime = AtomicLong(0)
    private var userInactiveJob: Job? = null

    fun startUserInactiveTimeCounter() {
        if (userInactiveJob != null && userInactiveJob!!.isActive) return

        userInactiveJob = viewModelScope.launch(Dispatchers.IO) {
            while (userInactiveTime.get() < Constants.USER_INACTIVITY_TIME_LIMIT &&
                userInactiveJob != null && !userInactiveJob?.isCancelled!!
            ) {
                delay(1000)
                userInactiveTime.incrementAndGet()
            }

            if (!isAppLocked()) {
                lockApp()
            }

            cancel()
        }
    }

    fun checkUserInactiveTimeStatus() {
        if (userInactiveTime.get() < Constants.USER_INACTIVITY_TIME_LIMIT) {
            if (userInactiveJob != null && userInactiveJob?.isCancelled == false) {
                userInactiveJob?.cancel()
                resetUserInactiveTimer()
            }
        }
    }

    private fun resetUserInactiveTimer() {
        userInactiveTime.set(0)
    }
}