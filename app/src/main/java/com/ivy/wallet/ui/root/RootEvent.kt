package com.ivy.wallet.ui.root

import android.content.Intent

sealed class RootEvent {

    data class Start(val intent: Intent) : RootEvent()

    object LockApp : RootEvent()
    object UnlockApp : RootEvent()

    sealed class Internal {
        object LoadTheme : RootEvent()
    }
}