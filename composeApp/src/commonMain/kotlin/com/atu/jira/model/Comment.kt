package com.atu.jira.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(

    val id: String? = null,

    @SerialName("ticket_id")
    val ticketId: String,

    val content: String,

    @SerialName("created_by")
    val createdBy: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("parent_id")
    val parentId: String? = null // 👈 KEY FIELD

)