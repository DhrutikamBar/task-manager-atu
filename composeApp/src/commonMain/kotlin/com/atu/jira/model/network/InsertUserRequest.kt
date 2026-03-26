package com.atu.jira.model.network

import kotlinx.serialization.Serializable

@Serializable
data class InsertUserRequest(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
    val is_super_admin: Boolean,
    val is_admin: Boolean
)
