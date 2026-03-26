package com.atu.jira.utils

import android.content.Context
import com.atu.jira.storage.SessionStorage

class AndroidSessionStorage(context: Context) : SessionStorage {

    private val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString("token", null)
    }

    override fun saveValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getValue(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}
