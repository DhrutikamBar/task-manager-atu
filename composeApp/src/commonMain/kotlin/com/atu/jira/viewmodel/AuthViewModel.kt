package com.atu.jira.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atu.jira.auth.AuthManager
import com.atu.jira.repo.login
import com.atu.jira.repo.signup
import com.atu.jira.utils.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<ResourceState<Unit>>(ResourceState.Idle)
    val authState: StateFlow<ResourceState<Unit>> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loginUser(email: String, password: String, onLoginSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) return
        
        viewModelScope.launch {
            _authState.value = ResourceState.Loading
            _isLoading.value = true
            _error.value = null
            try {
                val response = login(email, password)
                AuthManager.saveSession(
                    token = response.access_token,
                    uId = response.user.id,
                    userEmail = response.user.email,
                    userRole = response.user.role
                )
                _authState.value = ResourceState.Success(Unit)
                onLoginSuccess()
            } catch (e: Exception) {
                val msg = e.message ?: "Login failed"
                _error.value = msg
                _authState.value = ResourceState.Error(msg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signupUser(email: String, name: String, password: String, onSignupSuccess: () -> Unit, onLoginFallback: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) return
        
        viewModelScope.launch {
            _authState.value = ResourceState.Loading
            _isLoading.value = true
            _error.value = null
            try {
                val response = signup(email, password, name)
                if (response.access_token != null && response.user != null) {
                    AuthManager.saveSession(
                        token = response.access_token,
                        uId = response.user.id,
                        userEmail = response.user.email,
                        name = name,
                        userRole = response.user.role
                    )
                    _authState.value = ResourceState.Success(Unit)
                    onSignupSuccess()
                } else {
                    _authState.value = ResourceState.Idle
                    onLoginFallback()
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Signup failed"
                _error.value = msg
                _authState.value = ResourceState.Error(msg)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
