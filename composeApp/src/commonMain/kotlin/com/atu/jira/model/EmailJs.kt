package com.atu.jira.model

import kotlinx.serialization.Serializable

@Serializable
data class EmailJSRequest(
    val service_id: String,
    val template_id: String,
    val user_id: String,
    val template_params: TemplateParams
)

@Serializable
data class TemplateParams(
    val to_email: String,
    val ticket_code: String,
    val message: String
)