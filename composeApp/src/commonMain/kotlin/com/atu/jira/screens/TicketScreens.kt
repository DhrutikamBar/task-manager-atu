package com.atu.jira.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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
import com.atu.jira.viewmodel.TicketViewModel
import com.atu.jira.utils.ResourceState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import kotlinx.coroutines.launch
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
                    onMove = {
                        viewModel.moveTicket(
                            it,
                            Status.IN_PROGRESS.value,
                            project.id ?: 0L
                        )
                    },
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

        ResourceHandler(
            state = usersState,
            onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) }) { users ->
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedUser?.name ?: "Select Assignee",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assignee") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
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
                        TicketCardV2(
                            ticket = ticket,
                            onTicketClick = onTicketClick,
                            onMove = onMove
                        )
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
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(20.dp)
                            .background(borderColor.copy(alpha = 0.2f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ticket.assignedTo?.take(1)?.uppercase() ?: "?",
                            color = borderColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = ticket.assignedTo ?: "Unassigned",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
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
    val colors = listOf(
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFF42A5F5),
        Color(0xFF26A69A),
        Color(0xFFFFA726),
        Color(0xFF66BB6A)
    )
    return colors[(ticket.title.hashCode().absoluteValue) % colors.size]
}

@Composable
fun JiraCommentBox(
    onSend: (String) -> Unit
) {
    val state = rememberRichTextState()
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {

        // 🔹 COLLAPSED VIEW
        if (!isExpanded) {
            Text(
                "Add a comment...",
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true }
                    .padding(8.dp)
            )
        }

        // 🔹 EXPANDED VIEW
        else {

            // ✏️ TOOLBAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {

                IconButton(onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(fontWeight = FontWeight.Bold)
                    )
                }) {
                    Icon(Icons.Default.FormatBold, "Bold")
                }

                IconButton(onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(fontStyle = FontStyle.Italic)
                    )
                }) {
                    Icon(Icons.Default.FormatItalic, "Italic")
                }

                IconButton(onClick = {
                    state.toggleCodeSpan()
                }) {
                    Icon(Icons.Default.Code, "Code")
                }

                IconButton(onClick = {
                    state.toggleUnorderedList()
                }) {
                    Icon(Icons.AutoMirrored.Filled.FormatListBulleted, "List")
                }
            }

            // ✏️ EDITOR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {

                if (state.toHtml().isEmpty()) {
                    Text(
                        "Write a comment...",
                        color = Color.Gray
                    )
                }

                RichTextEditor(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(8.dp))

            // 🔹 ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = {
                    isExpanded = false
                    state.setHtml("")
                }) {
                    Text("Cancel")
                }

                Spacer(Modifier.width(8.dp))

                Button(onClick = {
                    val html = state.toHtml()
                    if (html.isBlank()) return@Button

                    onSend(html)

                    // reset
                    state.setHtml("")
                    isExpanded = false
                }) {
                    Text("Comment")
                }
            }
        }
    }
}

/*** In V4 we have updated Ticket Details UI Completely by adding rich text editor **/
@Composable
fun TicketDetailScreenV4(
    ticket: Ticket,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onClickEditTicket: (Ticket) -> Unit
) {
    val commentsState by viewModel.commentsState.collectAsState()
    val historyState by viewModel.historyState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(ticket.id) {
        viewModel.loadComments(ticket.id.toString())
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false,
            drawerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(360.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 🔹 HEADER
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Activity",
                                style = MaterialTheme.typography.titleLarge
                            )

                            IconButton(onClick = {
                                scope.launch { drawerState.close() }
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider()

                        // 🔹 CONTENT
                        ResourceHandler(state = historyState) { history ->
                            if (history.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No activity yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    items(history) { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // 🔹 TIMELINE DOT + LINE
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .background(
                                                            MaterialTheme.colorScheme.primary,
                                                            shape = CircleShape
                                                        )
                                                )

                                                Box(
                                                    modifier = Modifier
                                                        .width(2.dp)
                                                        .height(60.dp)
                                                        .background(Color.LightGray)
                                                )
                                            }

                                            Spacer(Modifier.width(12.dp))

                                            // 🔹 CONTENT
                                            Column(
                                                modifier = Modifier
                                                    .padding(bottom = 16.dp)
                                                    .weight(1f)
                                            ) {
                                                Text(
                                                    item.fieldName ?: "Field",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )

                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    "${item.oldValue} ->  ${item.newValue}",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )

                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    "By ${item.changedBy}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )

                                                Text(
                                                    "On ${item.changedAt}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) {
            // YOUR EXISTING SCREEN CONTENT HERE
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                // Basic detail view for now
                Column(Modifier.fillMaxSize()) {
                    CommonTopBar(
                        title = "Ticket Details",
                        showBack = true,
                        onBack = onBack,
                        onLogout = onLogout
                    )
                    HorizontalDivider()

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().absolutePadding(
                            left = 11.dp,
                            right = 11.dp,
                            top = 24.dp,
                            bottom = 11.dp
                        )
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    ticket.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.fillMaxWidth().weight(.9f)
                                )

                                Row(modifier = Modifier.fillMaxWidth().weight(.1f)) {
                                    IconButton(onClick = {
                                        onClickEditTicket(ticket)
                                    }, modifier = Modifier.weight(.5f)) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    IconButton(onClick = {
                                        scope.launch {
                                            viewModel.loadTicketHistory(ticket.id.toString())
                                            drawerState.open()
                                        }
                                    }, modifier = Modifier.weight(.5f)) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                ticket.description,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(11.dp)
                                    ).padding(10.dp)
                            )
                        }

                        item {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Add Comment",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            JiraCommentBox { htmlComment ->
                                viewModel.addCommentToTicket(ticket.id.toString(), htmlComment)
                            }
                        }

                        item {
                            Column(modifier = Modifier.weight(1f)) {
                                Spacer(Modifier.height(20.dp))
                                
                                ResourceHandler(
                                    state = commentsState,
                                    onLoading = { Text("Loading comments...") }
                                ) { comments ->
                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        "Comments",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    comments.forEach { comment ->
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            // 🔹 Avatar
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    comment.createdBy?.take(1)?.uppercase() ?: "U",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Spacer(Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                // 🔹 Name + Time
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        comment.createdBy ?: "User",
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        comment.createdAt ?: "",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.Gray
                                                    )
                                                }

                                                Spacer(Modifier.height(6.dp))

                                                // 🔹 Content
                                                val state = rememberRichTextState()
                                                LaunchedEffect(comment.content) {
                                                    state.setHtml(comment.content ?: "")
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(10.dp)
                                                ) {
                                                    RichText(
                                                        state = state,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }

                                                Spacer(Modifier.height(12.dp))

                                                HorizontalDivider(
                                                    color = Color.LightGray.copy(alpha = 0.4f)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskListScreen(viewModel: TicketViewModel = viewModel { TicketViewModel() }) {
    val allTicketsState by viewModel.allTicketsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllTickets()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("All My Tasks", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        ResourceHandler(
            state = allTicketsState,
            onLoading = { LoadingUI() }
        ) { tickets ->
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

@Composable
fun EditTicketScreen(
    ticket: Ticket,
    onTicketUpdated: (Ticket) -> Unit,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
) {
    var title by remember { mutableStateOf(ticket.title) }
    var description by remember { mutableStateOf(ticket.description) }
    var status by remember { mutableStateOf(ticket.status) }
    var projectId by remember { mutableStateOf(ticket.projectId) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Edit Ticket", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.updateTicket(
                    oldTicket = ticket,
                    title = title,
                    description = description,
                    projectId = projectId!!,
                    newStatus = status,
                    onTicketUpdated = onTicketUpdated
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
