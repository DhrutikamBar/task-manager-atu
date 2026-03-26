package com.atu.jira.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.components.CommonTopBar
import com.atu.jira.components.LoadingUI
import com.atu.jira.components.ResourceHandler
import com.atu.jira.model.Project
import com.atu.jira.model.Status
import com.atu.jira.model.Ticket
import com.atu.jira.model.User
import com.atu.jira.repo.getAllTickets
import com.atu.jira.viewmodel.TicketViewModel
import com.atu.jira.utils.ResourceState
import kotlin.math.absoluteValue

@Composable
fun TicketBoardScreen(
    project: Project,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onTicketClick: (Ticket) -> Unit,
    onBack: () -> Unit,
    onAddTicket: () -> Unit,
    onLogout: () -> Unit
) {
    val ticketsState by viewModel.ticketsState.collectAsState()

    LaunchedEffect(project.id) {
        viewModel.loadTickets(project.id ?: 0L)
    }

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(
            title = project.name,
            showBack = true,
            onBack = onBack,
            onLogout = onLogout
        )

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kanban Board", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onAddTicket) {
                Text("+ New Ticket")
            }
        }

        ResourceHandler(state = ticketsState) { tickets ->
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusColumn(
                    title = "To Do",
                    tickets = tickets.filter { it.status == Status.TODO.value },
                    onTicketClick = onTicketClick,
                    onMove = { viewModel.moveTicket(it, Status.IN_PROGRESS.value, project.id ?: 0L) },
                    modifier = Modifier.weight(1f)
                )

                StatusColumn(
                    title = "In Progress",
                    tickets = tickets.filter { it.status == Status.IN_PROGRESS.value },
                    onTicketClick = onTicketClick,
                    onMove = { viewModel.moveTicket(it, Status.DONE.value, project.id ?: 0L) },
                    modifier = Modifier.weight(1f)
                )

                StatusColumn(
                    title = "Done",
                    tickets = tickets.filter { it.status == Status.DONE.value },
                    onTicketClick = onTicketClick,
                    onMove = null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    project: Project,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onCreate: (Ticket) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val usersState by viewModel.usersState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        CommonTopBar(title = "New Ticket", showBack = true, onBack = onBack, onLogout = onLogout)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(16.dp))

        ResourceHandler(state = usersState, onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) }) { users ->
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedUser?.name ?: "Select Assignee",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assignee") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.name) },
                            onClick = {
                                selectedUser = user
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { viewModel.addTicket(title, description, project, selectedUser, onCreate) },
            enabled = title.isNotBlank() && actionState !is ResourceState.Loading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (actionState is ResourceState.Loading) {
              //  CircularProgressIndicator(size = 24.dp, color = Color.White)
            } else {
                Text("Create Ticket")
            }
        }
    }
}

@Composable
fun StatusColumn(
    title: String,
    tickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    onMove: ((Ticket) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxHeight()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = "${title.uppercase()} (${tickets.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tickets) { ticket ->
                        TicketCardV2(ticket = ticket, onTicketClick = onTicketClick, onMove = onMove)
                    }
                }
            }
        }
    }
}

@Composable
fun TicketCardV2(
    ticket: Ticket,
    onTicketClick: (Ticket) -> Unit,
    onMove: ((Ticket) -> Unit)? = null
) {
    val borderColor = remember(ticket) { getTicketColor(ticket) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onTicketClick(ticket) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(borderColor))
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = ticket.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(20.dp).background(borderColor.copy(alpha = 0.2f), shape = CircleShape), contentAlignment = Alignment.Center) {
                        Text(text = ticket.assignedTo?.take(1)?.uppercase() ?: "?", color = borderColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(text = ticket.assignedTo ?: "Unassigned", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                onMove?.let {
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { it(ticket) }, contentPadding = PaddingValues(0.dp)) {
                        Text("Move →", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

fun getTicketColor(ticket: Ticket): Color {
    val colors = listOf(Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFFFFA726), Color(0xFF66BB6A))
    return colors[(ticket.title.hashCode().absoluteValue) % colors.size]
}

@Composable
fun TicketDetailScreen(
    ticket: Ticket,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    // Basic detail view for now
    Column(Modifier.fillMaxSize()) {
        CommonTopBar(title = "Ticket Details", showBack = true, onBack = onBack, onLogout = onLogout)
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))
        Text(ticket.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(ticket.description, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun TaskListScreen() {
    var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            tickets = getAllTickets()
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("All My Tasks", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            LoadingUI()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tickets) { ticket ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(ticket.title, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
