package com.atu.jira.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String? = null,
    val is_super_admin: Boolean = false,
    val is_admin: Boolean = false
)

@Serializable
data class Project(
    val id: Long? = null,
    val name: String
)

@Serializable
data class Ticket(
    val id: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    var title: String,
    var description: String,
    var status: String,
    val priority: String? = "medium",
    @SerialName("project_id") val projectId: Long? = null,
    @SerialName("assigned_to") var assignedTo: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("due_date") val dueDate: String? = null
)

@Serializable
enum class Status(val value: String) {
    TODO("todo"),
    IN_PROGRESS("in_progress"),
    DONE("done")
}

@Serializable
data class AuthError(
    val code: Int? = null,
    val error_code: String? = null,
    val msg: String? = null
)

@Serializable
data class UserData(
    val id: String,
    val email: String
)

enum class Screen {
    LOGIN, SIGNUP, HOME, PROJECTS, BOARD, TICKET_DETAIL, CREATE_TICKET, CREATE_PROJECT
}

// Navigation Routes
@Serializable @SerialName("login") object LoginRoute
@Serializable @SerialName("signup") object SignupRoute
@Serializable @SerialName("home") object HomeRoute
@Serializable @SerialName("create_project") object CreateProjectRoute
@Serializable @SerialName("board") data class BoardRoute(val projectId: Long, val projectName: String)
@Serializable @SerialName("create_ticket") data class CreateTicketRoute(val projectId: Long, val projectName: String)

@Serializable @SerialName("ticket_detail") data class TicketDetailRoute(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String? = null,
    val assignedTo: String? = null,
    val projectId: Long? = null,
    val createdBy: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val dueDate: String? = null,
    val createdAt: String? = null
)

@Serializable @SerialName("edit_ticket") data class EditTicketRoute(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String? = null,
    val assignedTo: String? = null,
    val projectId: Long? = null,
    val createdBy: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val dueDate: String? = null,
    val createdAt: String? = null
)
