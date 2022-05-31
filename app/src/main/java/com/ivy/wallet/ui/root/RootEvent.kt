package com.ivy.wallet.ui.root

import android.content.Intent

sealed class RootEvent {

    data class Start(val intent: Intent) : RootEvent()
    data class LoadTheme(val isSystemInDarkMode: Boolean) : RootEvent()

    object LockApp : RootEvent()
    object UnlockApp : RootEvent()
}