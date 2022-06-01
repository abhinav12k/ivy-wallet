package com.ivy.wallet.io.network

import com.ivy.wallet.io.network.request.auth.AuthResponse
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.UserDao
import java.util.*

class IvySession(
    private val sharedPrefs: SharedPrefs,
    private val userDao: UserDao
) {
    private var userId: UUID? = null
    private var authToken: String? = null

    fun loadSession(userId: UUID?, authToken: String?) {
        this.userId = userId
        this.authToken = authToken
    }

    fun getSessionToken() = authToken ?: throw NoSessionException()

    fun getUserId(): UUID = userId ?: throw NoSessionException()

    fun getUserIdSafe(): UUID? = userId

    fun isLoggedIn(): Boolean {
        return userId != null && authToken != null
    }

    suspend fun initiate(authResponse: AuthResponse) {
        val user = authResponse.user
        userDao.save(user.toEntity())

        sharedPrefs.putString(SharedPrefs.SESSION_USER_ID, user.id.toString())
        sharedPrefs.putString(SharedPrefs.SESSION_AUTH_TOKEN, authResponse.sessionToken)

        userId = authResponse.user.id
        authToken = authResponse.sessionToken
    }

    fun logout() {
        sharedPrefs.remove(SharedPrefs.SESSION_USER_ID)
        sharedPrefs.remove(SharedPrefs.SESSION_AUTH_TOKEN)

        userId = null
        authToken = null
    }
}

class NoSessionException : IllegalStateException("No session.")