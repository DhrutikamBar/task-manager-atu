package com.atu.jira.auth

import com.atu.jira.storage.SessionStorage

object AuthManager {

    lateinit var sessionStorage: SessionStorage

    // Cache in memory for quick access
    private var _accessToken: String? = null
    var accessToken: String?
        get() = _accessToken ?: sessionStorage.getToken().also { _accessToken = it }
        set(value) {
            _accessToken = value
            value?.let { sessionStorage.saveToken(it) }
        }

    private var _userId: String? = null
    var userId: String?
        get() = _userId ?: sessionStorage.getValue("userId").also { _userId = it }
        set(value) {
            _userId = value
            value?.let { sessionStorage.saveValue("userId", it) }
        }

    private var _userName: String? = null
    var userName: String?
        get() = _userName ?: sessionStorage.getValue("userName").also { _userName = it }
        set(value) {
            _userName = value
            value?.let { sessionStorage.saveValue("userName", it) }
        }

    private var _email: String? = null
    var email: String?
        get() = _email ?: sessionStorage.getValue("email").also { _email = it }
        set(value) {
            _email = value
            value?.let { sessionStorage.saveValue("email", it) }
        }

    private var _role: String? = null
    var role: String?
        get() = _role ?: sessionStorage.getValue("role").also { _role = it }
        set(value) {
            _role = value
            value?.let { sessionStorage.saveValue("role", it) }
        }

    fun saveSession(token: String, uId: String? = null, userEmail: String? = null, name: String? = null, userRole: String? = null) {
        this.accessToken = token
        this.userId = uId
        this.email = userEmail
        this.userName = name
        this.role = userRole
    }

    fun isLoggedIn(): Boolean = accessToken != null

    fun logout() {
        _accessToken = null
        _userId = null
        _userName = null
        _email = null
        _role = null
        sessionStorage.clear()
    }
}
