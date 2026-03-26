package com.atu.jira.utils

sealed class ResourceState<out T> {
    data object Idle : ResourceState<Nothing>()
    data object Loading : ResourceState<Nothing>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error(val message: String) : ResourceState<Nothing>()
}
