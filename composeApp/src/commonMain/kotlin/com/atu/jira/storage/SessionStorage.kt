package com.atu.jira.storage

interface SessionStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveValue(key: String, value: String)
    fun getValue(key: String): String?
    fun clear()
}
