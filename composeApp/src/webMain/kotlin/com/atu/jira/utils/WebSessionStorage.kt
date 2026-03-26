package com.atu.jira.utils

import com.atu.jira.storage.SessionStorage
import kotlinx.browser.window

class WebSessionStorage : SessionStorage {

    override fun saveToken(token: String) {
        window.localStorage.setItem("token", token)
    }

    override fun getToken(): String? {
        return window.localStorage.getItem("token")
    }

    override fun saveValue(key: String, value: String) {
        window.localStorage.setItem(key, value)
    }

    override fun getValue(key: String): String? {
        return window.localStorage.getItem(key)
    }

    override fun clear() {
        window.localStorage.clear()
    }
}
