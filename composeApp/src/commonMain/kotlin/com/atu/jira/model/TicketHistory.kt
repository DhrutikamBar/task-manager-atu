package com.atu.jira.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketHistory(

    val id: String? = null,

    @SerialName("ticket_id")
    val ticketId: String,

    @SerialName("field_name")
    val fieldName: String,

    @SerialName("old_value")
    val oldValue: String?,

    @SerialName("new_value")
    val newValue: String,

    @SerialName("changed_by")
    val changedBy: String? = null,

    @SerialName("changed_at")
    val changedAt: String? = null
)