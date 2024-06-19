package com.example.omlaut_mobile_app

import android.content.Context
import android.content.SharedPreferences

/**
 * UserSession manages user login sessions using SharedPreferences.
 *
 * @property context The context used to access SharedPreferences.
 */
class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
    }
}
