package com.atu.jira.model.network

import kotlinx.serialization.Serializable

@Serializable
data class LogInResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val expires_at: Long,
    val refresh_token: String,
    val user: SupabaseUser,
    val weak_password: WeakPassword? = null
)

@Serializable
data class SignUpResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val expires_at: Long,
    val refresh_token: String,
    val user: SupabaseUser
)

@Serializable
data class SupabaseUser(
    val id: String,
    val aud: String,
    val role: String,
    val email: String,
    val email_confirmed_at: String? = null,
    val confirmed_at: String? = null,
    val phone: String? = null,
    val last_sign_in_at: String? = null,
    val app_metadata: AppMetadata? = null,
    val user_metadata: UserMetadata? = null,
    val identities: List<Identity>? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val is_anonymous: Boolean? = null
)

@Serializable
data class AppMetadata(
    val provider: String? = null,
    val providers: List<String>? = null
)

@Serializable
data class UserMetadata(
    val email: String? = null,
    val email_verified: Boolean? = null,
    val phone_verified: Boolean? = null,
    val sub: String? = null
)

@Serializable
data class Identity(
    val identity_id: String,
    val id: String,
    val user_id: String,
    val identity_data: IdentityData? = null,
    val provider: String? = null,
    val last_sign_in_at: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val email: String? = null
)

@Serializable
data class IdentityData(
    val email: String? = null,
    val email_verified: Boolean? = null,
    val phone_verified: Boolean? = null,
    val sub: String? = null
)

@Serializable
data class WeakPassword(
    val message: String? = null
)
