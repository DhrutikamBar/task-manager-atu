package com.atu.jira.repo

import com.atu.jira.auth.AuthManager
import com.atu.jira.model.*
import com.atu.jira.model.network.*
import com.atu.jira.repo.SupaBaseClient.client
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlin.time.Clock


private const val API_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRmaGd3dGhvbXRncGFsa2hsZnd6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyNTg1MzUsImV4cCI6MjA4OTgzNDUzNX0.FyXXXeTZbVC4Xm8ok8fR5gHhYrsA6PK9UUXBREw9YK4"
private const val RESEND_API_KEY = "re_a14Aagp3_KYd28RjM3KKywjcKg5M6ftJJ"
private const val EMAIL_JS_SERVICE_ID = "service_nie1afr"
private const val EMAIL_JS_EMAIL_TEMPLATE_ID_FOR_TICKET_ASSIGNMENT = "template_9kebpbp"
private const val EMAIL_JS_EMAIL_TEMPLATE_ID_FOR_MENTIONS = "template_7rl9btb"
private const val EMAIL_JS_PUBLIC_KEY = "FyceZqhfGUipB1Zuy"

suspend fun getProjects(): List<Project> {
    return SupaBaseClient.client.get("/rest/v1/projects") {
        headers {
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }
    }.body()
}

suspend fun createProject(project: Project) {
    SupaBaseClient.client.post("/rest/v1/projects") {
        contentType(ContentType.Application.Json)

        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }
        setBody(listOf(project))
    }
}

suspend fun getTickets(projectId: String): List<Ticket> {
    return SupaBaseClient.client
        .get("/rest/v1/tickets?project_id=eq.$projectId") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }
        .body()
}

suspend fun createTicket(ticket: Ticket) {
    SupaBaseClient.client.post("/rest/v1/tickets") {
        contentType(ContentType.Application.Json)

        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }

        setBody(listOf(ticket))
    }
}

suspend fun createTicketWithRPC(request: CreateTicketRequest): Ticket {
    val response: List<Ticket> =
        SupaBaseClient.client.post("/rest/v1/rpc/create_ticket") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
                append("Content-Type", "application/json")
                append("Prefer", "return=representation")
            }
            setBody(request)
        }.body()

    return response.first() // ✅ extract first item
}


suspend fun getTicketByTicketCode(ticketCode: String): Ticket? {
    return SupaBaseClient.client.get("/rest/v1/tickets") {
        parameter("ticket_code", "eq.$ticketCode")
        parameter("select", "*")
        headers {
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
            append("Content-Type", "application/json")
            append("Prefer", "return=representation")
        }
    }.body<List<Ticket>>().firstOrNull()
}


suspend fun sendEmailEmailJS(
    toEmail: String,
    ticketCode: String,
    message: String,
    actionType: String
) {
    val request = EmailJSRequest(
        service_id = EMAIL_JS_SERVICE_ID,
        template_id = if (actionType == "assign") EMAIL_JS_EMAIL_TEMPLATE_ID_FOR_TICKET_ASSIGNMENT else EMAIL_JS_EMAIL_TEMPLATE_ID_FOR_MENTIONS,
        user_id = EMAIL_JS_PUBLIC_KEY,
        template_params = TemplateParams(
            to_email = toEmail,
            ticket_code = ticketCode,
            message = message
        )
    )

    SupaBaseClient.client.post("https://api.emailjs.com/api/v1.0/email/send") {
        contentType(ContentType.Application.Json)
        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }
        setBody(request)
    }
}

suspend fun updateTicketStatus(ticketId: String, status: String) {
    SupaBaseClient.client.patch("/rest/v1/tickets?id=eq.$ticketId") {
        contentType(ContentType.Application.Json)
        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }
        setBody(mapOf("status" to status))
    }
}

suspend fun getAllTickets(): List<Ticket> {
    return SupaBaseClient.client
        .get("/rest/v1/tickets") {
            headers {
                append("Prefer", "return=minimal")
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }
        .body()
}


suspend fun login(email: String, password: String): LogInResponse {

    val response = SupaBaseClient.client.post("/auth/v1/token?grant_type=password") {

        contentType(ContentType.Application.Json)

        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }

        setBody(
            mapOf(
                "email" to email,
                "password" to password
            )
        )
    }

    val responseText = response.bodyAsText()
    println("LOGIN RESPONSE: $responseText")

    return if (response.status.value in 200..299) {
        Json.decodeFromString<LogInResponse>(responseText)
    } else {
        val error = Json.decodeFromString<AuthError>(responseText)
        throw Exception(error.msg ?: "Login failed")
    }
}


suspend fun signup(
    email: String,
    password: String,
    name: String
): SignUpResponse {

    val response = SupaBaseClient.client.post("/auth/v1/signup") {

        contentType(ContentType.Application.Json)

        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }

        setBody(
            mapOf(
                "email" to email,
                "password" to password
            )
        )
    }

    val responseText = response.bodyAsText()
    println("SIGNUP RESPONSE: $responseText")

    if (!response.status.isSuccess()) {
        val error = Json.decodeFromString<AuthError>(responseText)
        throw Exception(error.msg ?: "Signup failed")
    }

    val signupResponse = Json.decodeFromString<SignUpResponse>(responseText)

    val userId = signupResponse.user.id

    insertUser(userId, email, name)

    return signupResponse
}

suspend fun insertUser(userId: String, email: String, name: String) {

    val request = InsertUserRequest(
        id = userId,
        email = email,
        name = name,
        role = "",
        is_super_admin = false,
        is_admin = false
    )


    SupaBaseClient.client.post("/rest/v1/users") {

        contentType(ContentType.Application.Json)

        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")
        }

        setBody(
            listOf(
                request
            )
        )
    }
}


suspend fun getUsers(): List<User> {
    return SupaBaseClient.client
        .get("/rest/v1/users") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }
        .body()
}

suspend fun getComments(ticketId: String): List<Comment> {
    return SupaBaseClient.client
        .get("/rest/v1/comments?ticket_id=eq.$ticketId&order=created_at.asc") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }
        .body()
}

suspend fun addComment(comment: Comment) {
    SupaBaseClient.client.post("/rest/v1/comments") {
        contentType(ContentType.Application.Json)

        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")

        }

        setBody(listOf(comment))
    }
}

suspend fun addTicketHistory(history: TicketHistory) {
    SupaBaseClient.client.post("/rest/v1/ticket_history") {
        contentType(ContentType.Application.Json)
        headers {
            append("Prefer", "return=minimal")
            append("apikey", API_KEY)
            append("Authorization", "Bearer $API_KEY")

        }
        setBody(listOf(history))
    }
}

suspend fun searchUsers(query: String): List<User> {
    return SupaBaseClient.client
        .get("/rest/v1/users?name=ilike.*$query*") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }.body()
}

suspend fun searchTickets(query: String): List<Ticket> {
    return SupaBaseClient.client
        .get("/rest/v1/tickets?or=(title.ilike.*$query*,description.ilike.*$query*)") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }.body()
}

suspend fun updateTicketWithHistory(
    oldTicket: Ticket,
    newTicket: Ticket
) {
    try {
        // Prepare the payload dynamically mapping only fields that aren't null or changed
        val patchPayload = mutableMapOf<String, String?>()
        patchPayload["title"] = newTicket.title
        patchPayload["description"] = newTicket.description
        patchPayload["status"] = newTicket.status
        newTicket.priority?.let { patchPayload["priority"] = it }
        newTicket.assignedTo?.let { patchPayload["assigned_to"] = it }
        newTicket.startTime?.let { patchPayload["start_time"] = it }
        newTicket.endTime?.let { patchPayload["end_time"] = it }
        newTicket.dueDate?.let { patchPayload["due_date"] = it }
        patchPayload["updated_at"] = Clock.System.now().toString()

        // 1️⃣ Update ticket
        SupaBaseClient.client.patch("/rest/v1/tickets?id=eq.${oldTicket.id}") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
                append("Content-Type", "application/json")
                append("Prefer", "return=minimal")
            }
            setBody(patchPayload)
        }

        // 2️⃣ Prepare history entries
        val historyEntries = mutableListOf<Map<String, Any>>()

        fun checkChange(fieldName: String, oldVal: String?, newVal: String?) {
            if (oldVal != newVal) {
                historyEntries.add(
                    mapOf(
                        "ticket_id" to oldTicket.id.toString(),
                        "field_name" to fieldName,
                        "old_value" to (oldVal ?: ""),
                        "new_value" to (newVal ?: ""),
                        "changed_by" to (AuthManager.userId ?: "user_1")
                    )
                )
            }
        }

        checkChange("title", oldTicket.title, newTicket.title)
        checkChange("description", oldTicket.description, newTicket.description)
        checkChange("status", oldTicket.status, newTicket.status)
        checkChange("priority", oldTicket.priority, newTicket.priority)
        checkChange("assigned_to", oldTicket.assignedTo, newTicket.assignedTo)
        checkChange("start_time", oldTicket.startTime, newTicket.startTime)
        checkChange("end_time", oldTicket.endTime, newTicket.endTime)
        checkChange("due_date", oldTicket.dueDate, newTicket.dueDate)

        // 3️⃣ Insert history (only if something changed)
        if (historyEntries.isNotEmpty()) {
            SupaBaseClient.client.post("/rest/v1/ticket_history") {
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                    append("Content-Type", "application/json")
                    append("Prefer", "return=minimal")
                }
                setBody(historyEntries)
            }
        }

    } catch (e: Exception) {
        println("Update ticket failed: ${e.message}")
    }
}

suspend fun getTicketHistory(ticketId: String): List<TicketHistory> {
    return SupaBaseClient.client
        .get("/rest/v1/ticket_history?ticket_id=eq.$ticketId&order=changed_at.desc") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }
        .body()
}

suspend fun getUserDetails(userId: String): User {
    return SupaBaseClient.client
        .get("/rest/v1/users?id=eq.$userId") {
            headers {
                append("Prefer", "return=minimal")
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
            }
        }
        .body<List<User>>()
        .first()
}