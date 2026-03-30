package com.atu.jira.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atu.jira.components.JiraTextField
import com.atu.jira.model.Ticket
import com.atu.jira.model.User
import com.atu.jira.repo.searchTickets
import com.atu.jira.repo.searchUsers
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onTicketClick: (Ticket) -> Unit,
    onUserClick: (User) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔹 Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            JiraTextField(
                value = query,
                onValueChange = { query = it },
                label = "Search tickets, people...",
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        HorizontalDivider()

        // 🔹 Results
        SearchResultsList(
            results = results,
            isLoading = isLoading,
            onTicketClick = onTicketClick,
            onUserClick = onUserClick
        )
    }

    // 🔥 Debounce logic
    LaunchedEffect(query) {
        if (query.isBlank()) {
            results = emptyList()
            isLoading = false
            return@LaunchedEffect
        }

        delay(300)

        isLoading = true
        println("SEARCHING FOR: $query")

        try {
            val tickets = searchTickets(query)
            val users = searchUsers(query)
            
            println("FOUND ${tickets.size} tickets and ${users.size} users")

            results =
                tickets.map { SearchResult.TicketItem(it) } +
                        users.map { SearchResult.UserItem(it) }

        } catch (e: Exception) {
            println("SEARCH ERROR: ${e.message}")
            e.printStackTrace()
        }

        isLoading = false
    }
}


sealed class SearchResult {
    data class TicketItem(val ticket: Ticket) : SearchResult()
    data class UserItem(val user: User) : SearchResult()
}


@Composable
fun SearchResultsList(
    results: List<SearchResult>,
    isLoading: Boolean,
    onTicketClick: (Ticket) -> Unit,
    onUserClick: (User) -> Unit
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val tickets = results.filterIsInstance<SearchResult.TicketItem>()
    val users = results.filterIsInstance<SearchResult.UserItem>()

    if (results.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No results found", color = Color.Gray)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        if (tickets.isNotEmpty()) {
            item { SectionHeader("Tickets") }

            items(tickets) {
                TicketSearchItem(it.ticket, onTicketClick)
            }
        }

        /*if (users.isNotEmpty()) {
            item { SectionHeader("People") }

            items(users) {
                UserSearchItem(it.user, onUserClick)
            }
        }*/
    }
}

@Composable
fun TicketSearchItem(ticket: Ticket, onClick: (Ticket) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(ticket) }
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(16.dp)
    ) {
        Text(
            text = ticket.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = ticket.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            maxLines = 1
        )
    }
}

@Composable
fun UserSearchItem(user: User, onClick: (User) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(user) }
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(1).uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        fontWeight = FontWeight.Bold
    )
}