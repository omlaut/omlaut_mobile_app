package com.example.omlaut_mobile_app

import android.content.Context
import android.content.SharedPreferences

data class User(val name: String, val phone: String)

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_NAME = "user_name"
        const val USER_PHONE = "user_phone"
    }

    fun saveAuthToken(token: String?) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserDetails(user: User?) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, user?.name)
        editor.putString(USER_PHONE, user?.phone)
        editor.apply()
    }

    fun fetchUserDetails(): User? {
        val name = prefs.getString(USER_NAME, null)
        val phone = prefs.getString(USER_PHONE, null)
        return if (name != null && phone != null) {
            User(name, phone)
        } else {
            null
        }
    }
}
