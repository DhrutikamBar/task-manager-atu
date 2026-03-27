package com.atu.jira.repo

import com.atu.jira.auth.AuthManager
import com.atu.jira.model.*
import com.atu.jira.model.network.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
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

suspend fun getTickets(projectId: Long): List<Ticket> {
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
        .get("/rest/v1/comments?ticket_id=eq.$ticketId&order=created_at.asc"){
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

suspend fun updateTicketWithHistory(
    ticket: Ticket,
    newDescription: String,
    newStatus: String
) {
    try {
        // 1️⃣ Fetch current ticket (old values)
        val oldTicket: Ticket = SupaBaseClient.client
            .get("/rest/v1/tickets?id=eq.${ticket.id}") {
                headers {
                    append("apikey", API_KEY)
                    append("Authorization", "Bearer $API_KEY")
                }
            }
            .body<List<Ticket>>()
            .first()

        // 2️⃣ Update ticket
        SupaBaseClient.client.patch("/rest/v1/tickets?id=eq.${ticket.id}") {
            headers {
                append("apikey", API_KEY)
                append("Authorization", "Bearer $API_KEY")
                append("Content-Type", "application/json")
                append("Prefer", "return=minimal")
            }
            setBody(
                mapOf(
                    "description" to newDescription,
                    "status" to newStatus,
                    "updated_at" to Clock.System.now().toString()
                )
            )
        }

        // 3️⃣ Prepare history entries
        val historyEntries = mutableListOf<Map<String, Any>>()

        if (oldTicket.description != newDescription) {
            historyEntries.add(
                mapOf(
                    "ticket_id" to ticket.id.toString(),
                    "field_name" to "description",
                    "old_value" to (oldTicket.description ?: ""),
                    "new_value" to newDescription,
                    "changed_by" to "user_1"
                )
            )
        }

        if (oldTicket.status != newStatus) {
            historyEntries.add(
                mapOf(
                    "ticket_id" to ticket.id.toString(),
                    "field_name" to "status",
                    "old_value" to oldTicket.status,
                    "new_value" to newStatus,
                    "changed_by" to "user_1"
                )
            )
        }

        // 4️⃣ Insert history (only if something changed)
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
