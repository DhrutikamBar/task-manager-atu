package com.atu.jira.model

import com.atu.jira.auth.AuthManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Ticket.toCreateRequest(): CreateTicketRequest {
    return CreateTicketRequest(
        projectId = projectId!!,
        title = title,
        description = description,
        createdBy = AuthManager.userId!!,
        priority = priority ?: "MEDIUM",
        status = status,
        assignedTo = assignedTo,
        startTime = startTime,
        endTime = endTime,
        dueDate = dueDate
    )
}

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
data class Project @OptIn(ExperimentalUuidApi::class) constructor(

    @SerialName("id")
    val id: String = Uuid.random().toString(),   // UUID → String

    @SerialName("name")
    val name: String,

    @SerialName("project_code")
    val projectCode: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("created_by")
    val createdBy: String? = null,   // ideally UUID later

    @SerialName("ticket_sequence")
    val ticketSequence: Long = 0,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true
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
    @SerialName("project_id") val projectId: String? = null,
    @SerialName("assigned_to") var assignedTo: String? = null,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    @SerialName("due_date") val dueDate: String? = null,
    @SerialName("ticket_code") val ticketCode: String? = "",
    @SerialName("ticket_type") val ticketType: String? = TicketType.TASK.name,
)

@Serializable
data class CreateTicketRequest(
    @SerialName("p_project_id") val projectId: String,
    @SerialName("p_title") val title: String,
    @SerialName("p_description") val description: String,
    @SerialName("p_created_by") val createdBy: String,
    @SerialName("p_priority") val priority: String = "MEDIUM",
    @SerialName("p_status") val status: String = "OPEN",
    @SerialName("p_assigned_to") val assignedTo: String? = null,
    @SerialName("p_start_time") val startTime: String? = null,
    @SerialName("p_end_time") val endTime: String? = null,
    @SerialName("p_due_date") val dueDate: String? = null
)

@Serializable
data class TicketResponse(
    @SerialName("ticket_id")
    val ticketId: String,

    @SerialName("ticket_code")
    val ticketCode: String
)

@Serializable
enum class Status(val value: String) {
    TODO("TODO"),
    ON_QA("ON_QA"),
    ON_HOLD("ON_HOLD"),
    IN_PROGRESS("IN_PROGRESS"),
    CLOSED("CLOSED")
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
    LOGIN, SIGNUP, HOME, PROJECTS, BOARD, TICKET_DETAIL, CREATE_TICKET, CREATE_PROJECT, SEARCH
}

enum class TicketType(val label: String) {
    BUG("Bug"),
    FEATURE("Feature"),
    IMPROVEMENT("Improvement"),
    TASK("Task")
}


// Navigation Routes
@Serializable
@SerialName("login")
object LoginRoute
@Serializable
@SerialName("signup")
object SignupRoute
@Serializable
@SerialName("home")
object HomeRoute
@Serializable
@SerialName("create_project")
object CreateProjectRoute
@Serializable
@SerialName("search")
object SearchRoute
@Serializable
@SerialName("board")
data class BoardRoute(val projectId: String, val projectName: String)
@Serializable
@SerialName("create_ticket")
data class CreateTicketRoute(val projectId: String, val projectName: String)


@Serializable
@SerialName("ticket")
data class TicketDetailRouteV2(
    val ticketCode: String? = null
)

@Serializable
@SerialName("edit_ticket")
data class EditTicketRoute(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String? = null,
    val assignedTo: String? = null,
    val projectId: String? = null,
    val createdBy: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val dueDate: String? = null,
    val createdAt: String? = null
)
