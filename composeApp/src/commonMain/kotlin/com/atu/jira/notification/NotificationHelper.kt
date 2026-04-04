package com.atu.jira.notification

import com.atu.jira.auth.AuthManager
import com.atu.jira.model.Ticket
import com.atu.jira.repo.sendEmailEmailJS
import com.atu.jira.users.UserManager

object NotificationHelper {

    suspend fun notifyAssignedUser(userId: String, ticket: Ticket,ticketCode: String?="") {
        if (userId == AuthManager.userId) return

        val user = UserManager.getUser(userId) ?: return

        sendEmailEmailJS(
            toEmail = user.email,
            ticketCode = ticket.ticketCode ?: ticketCode?:"",
            message = "You have been assigned a ticket",
            actionType = "assign"
        )
    }

    suspend fun notifyUpdatedAssignedUser(userId: String, ticket: Ticket,ticketCode: String?="") {
        if (userId == AuthManager.userId) return

        val user = UserManager.getUser(userId) ?: return
        println("old_ticket_log 2-> $ticketCode")
        sendEmailEmailJS(
            toEmail = user.email,
            ticketCode = ticketCode?:"",
            message = "You have been assigned a ticket",
            actionType = "assign"
        )
    }

    suspend fun notifyMentionedUsers(
        mentionedIds: List<String>,
        ticket: Ticket,
        message: String
    ) {
        mentionedIds
            .filter { it != AuthManager.userId }
            .distinct()
            .mapNotNull { UserManager.getUser(it) }
            .forEach {
                sendEmailEmailJS(
                    toEmail = it.email,
                    ticketCode = ticket.ticketCode ?: "",
                    message = message,
                    actionType = "mention"
                )
            }
    }
}