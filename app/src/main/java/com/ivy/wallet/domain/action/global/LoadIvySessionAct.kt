package com.ivy.wallet.domain.action.global

import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.persistence.SharedPrefs
import java.util.*
import javax.inject.Inject

class LoadIvySessionAct @Inject constructor(
    private val sharedPrefs: SharedPrefs,
    private val ivySession: IvySession
) : FPAction<Unit, Unit>() {
    override suspend fun Unit.compose(): suspend () -> Unit = suspend {
        val userId = sharedPrefs.getString(SharedPrefs.SESSION_USER_ID, null)
            ?.let { UUID.fromString(it) }
        val authToken = sharedPrefs.getString(SharedPrefs.SESSION_AUTH_TOKEN, null)

        ivySession.loadSession(userId, authToken)
    }
}