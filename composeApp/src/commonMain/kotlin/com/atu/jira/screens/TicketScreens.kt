package com.atu.jira.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.auth.AuthManager
import com.atu.jira.components.CommentShimmerItem
import com.atu.jira.components.CommonTopBar
import com.atu.jira.components.DevicePosture
import com.atu.jira.components.HistoryShimmerItem
import com.atu.jira.components.JiraButton
import com.atu.jira.components.JiraCard
import com.atu.jira.components.JiraTextField
import com.atu.jira.components.MainButton
import com.atu.jira.components.ResourceHandler
import com.atu.jira.components.TicketShimmerItem
import com.atu.jira.components.calculateDevicePosture
import com.atu.jira.model.Comment
import com.atu.jira.model.Project
import com.atu.jira.model.Status
import com.atu.jira.model.Ticket
import com.atu.jira.model.TicketHistory
import com.atu.jira.model.TicketType
import com.atu.jira.model.User
import com.atu.jira.notification.NotificationHelper
import com.atu.jira.users.UserManager
import com.atu.jira.viewmodel.TicketViewModel
import com.atu.jira.utils.ResourceState
import com.atu.jira.utils.Utils
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


// --- Date Helpers ---
fun formatEpochMillisToDate(millis: Long): String {
    val daysSinceEpoch = millis / 86400000L
    val z = daysSinceEpoch + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = (z - era * 146097).toInt()
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = (yoe + era * 400).toInt()
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = mp + if (mp < 10) 3 else -9
    val year = y + if (m <= 2) 1 else 0

    val monthStr = m.toString().padStart(2, '0')
    val dayStr = d.toString().padStart(2, '0')
    return "$year-$monthStr-$dayStr"
}

fun parseDateToEpochMillis(dateStr: String): Long? {
    if (dateStr.isBlank()) return null
    try {
        val parts = dateStr.split("-")
        if (parts.size != 3) return null
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jdn = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        val epochDays = jdn - 2440588
        return epochDays * 86400000L
    } catch (e: Exception) {
        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val initialMillis = remember(selectedDate) { parseDateToEpochMillis(selectedDate) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)


    /* val datePickerState = rememberDatePickerState()

     LaunchedEffect(selectedDate) {
         val millis = parseDateToEpochMillis(selectedDate)
         if (millis != null) {
             datePickerState.selectedDateMillis = millis
         }
     }*/


    Box {
        JiraTextField(
            value = selectedDate,
            onValueChange = {},
            label = label,
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        // Invisible clickable overlay to capture touch events
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable { expanded = true }
                .pointerHoverIcon(PointerIcon.Hand)
        )
    }

    if (expanded) {
        DatePickerDialog(
            onDismissRequest = { expanded = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(formatEpochMillisToDate(millis))
                        }
                        expanded = false
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { expanded = false },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// --- End Date Helpers ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        JiraTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = label,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDropdown(
    users: List<User>,
    selectedUser: User?,
    onUserSelected: (User) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        JiraTextField(
            value = selectedUser?.name ?: "Unassigned",
            onValueChange = {},
            readOnly = true,
            label = "Assignee",
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = { Text(user.name) },
                    onClick = {
                        onUserSelected(user)
                        expanded = false
                    },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                )
            }
        }
    }
}

@Composable
fun UserSearchView(
    users: List<User>,
    selectedUser: User?,
    onUserSelected: (User) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    val filteredUsers = if (searchText.isEmpty()) {
        users
    } else {
        users.filter {
            it.name.contains(searchText, ignoreCase = true)
        }
    }

    Column {

        JiraTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                isFocused = true
            },
            label = "Search User",
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                        isFocused = true
                        UserManager.getUser(AuthManager.userId)?.let { onUserSelected(it) }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.isFocused
                }
        )

        if (isFocused && searchText.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                LazyColumn {
                    items(filteredUsers) { user ->
                        Text(
                            text = user.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUserSelected(user)
                                    searchText = user.name
                                    isFocused = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketBoardScreen(
    project: Project,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onTicketClick: (Ticket) -> Unit,
    onBack: () -> Unit,
    onAddTicket: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {
    val ticketsState by viewModel.ticketsState.collectAsState()

    LaunchedEffect(project.id) {
        viewModel.loadTickets(project.id)
    }

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(
            title = project.name,
            showBack = true,
            onBack = onBack,
            onLogout = onLogout,
            onSearch = onSearchClick
        )

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kanban Board", style = MaterialTheme.typography.titleLarge)

            Box(modifier = Modifier.width(200.dp), contentAlignment = Alignment.Center) {
                JiraButton(
                    text = "+ New Ticket",
                    enabled = UserManager.isAdmin(AuthManager.userId) || UserManager.isSuperAdmin(
                        AuthManager.userId
                    ),
                    onClick = {
                        onAddTicket()
                    }
                )
            }

        }

        ResourceHandler(
            state = ticketsState,
            onLoading = {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                    val isWideScreen = maxWidth > 600.dp
                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) {
                                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    BoardColumnSkeleton()
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            repeat(3) {
                                BoardColumnSkeleton()
                            }
                        }
                    }
                }
            }
        ) { tickets ->
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                val isWideScreen = maxWidth > 600.dp

                if (isWideScreen) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
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
                                    project.id
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        StatusColumn(
                            title = "In Progress",
                            tickets = tickets.filter { it.status == Status.IN_PROGRESS.value },
                            onTicketClick = onTicketClick,
                            onMove = { viewModel.moveTicket(it, Status.DONE.value, project.id) },
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
                } else {
                    // Mobile: Vertically stacked columns
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatusColumn(
                            title = "To Do",
                            tickets = tickets.filter { it.status == Status.TODO.value },
                            onTicketClick = onTicketClick,
                            onMove = {
                                viewModel.moveTicket(
                                    it,
                                    Status.IN_PROGRESS.value,
                                    project.id
                                )
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                        )

                        StatusColumn(
                            title = "In Progress",
                            tickets = tickets.filter { it.status == Status.IN_PROGRESS.value },
                            onTicketClick = onTicketClick,
                            onMove = { viewModel.moveTicket(it, Status.DONE.value, project.id) },
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                        )

                        StatusColumn(
                            title = "Done",
                            tickets = tickets.filter { it.status == Status.DONE.value },
                            onTicketClick = onTicketClick,
                            onMove = null,
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoardColumnSkeleton() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(400.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(4) {
                    TicketShimmerItem()
                }
            }
        }
    }
}

@Composable
fun JiraRichTextEditor(
    state: com.mohamedrejeb.richeditor.model.RichTextState,
    modifier: Modifier = Modifier,
    placeholderText: String = "Description..."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        // ✏️ TOOLBAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(Icons.Default.FormatBold, "Bold")
            }

            IconButton(
                onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(Icons.Default.FormatItalic, "Italic")
            }

            IconButton(
                onClick = { state.toggleCodeSpan() },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(Icons.Default.Code, "Code")
            }

            IconButton(
                onClick = { state.toggleUnorderedList() },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Icon(Icons.AutoMirrored.Filled.FormatListBulleted, "List")
            }
        }

        // ✏️ EDITOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 250.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            if (state.toHtml().isEmpty()) {
                Text(
                    placeholderText,
                    color = Color.Gray
                )
            }

            RichTextEditor(
                state = state,
                modifier = Modifier.fillMaxSize().pointerHoverIcon(PointerIcon.Text)
            )
        }
    }
}

@Composable
fun CreateTicketScreen(
    project: Project,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onCreate: (Ticket) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    val descriptionState = rememberRichTextState()
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var status by remember { mutableStateOf(Status.TODO.value) }
    var priority by remember { mutableStateOf("medium") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    val usersState by viewModel.usersState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CommonTopBar(
            title = "New Ticket",
            showBack = true,
            onBack = onBack,
            onLogout = onLogout,
            onSearch = onSearchClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            JiraTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Description",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            JiraRichTextEditor(
                state = descriptionState,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            SimpleDropdown(
                label = "Status",
                options = listOf(Status.TODO.value, Status.IN_PROGRESS.value, Status.DONE.value),
                selectedOption = status,
                onOptionSelected = { status = it }
            )

            Spacer(Modifier.height(16.dp))

            SimpleDropdown(
                label = "Priority",
                options = listOf("low", "medium", "high", "critical"),
                selectedOption = priority,
                onOptionSelected = { priority = it }
            )

            Spacer(Modifier.height(16.dp))

            ResourceHandler(
                state = usersState,
                onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            ) { users ->
                UserDropdown(
                    users = users,
                    selectedUser = selectedUser,
                    onUserSelected = { selectedUser = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            DatePickerField(
                label = "Start Time (YYYY-MM-DD)",
                selectedDate = startTime,
                onDateSelected = { startTime = it }
            )

            Spacer(Modifier.height(16.dp))

            DatePickerField(
                label = "End Time (YYYY-MM-DD)",
                selectedDate = endTime,
                onDateSelected = { endTime = it }
            )

            Spacer(Modifier.height(16.dp))

            DatePickerField(
                label = "Due Date (YYYY-MM-DD)",
                selectedDate = dueDate,
                onDateSelected = { dueDate = it }
            )

            Spacer(Modifier.height(32.dp))

            MainButton(
                onClick = {
                    viewModel.createTicketWithRPCFunction(
                        Ticket(
                            title = title,
                            description = descriptionState.toHtml(),
                            status = status,
                            priority = priority,
                            projectId = project.id,
                            assignedTo = selectedUser?.id,
                            createdBy = AuthManager.userId,
                            startTime = startTime.takeIf { it.isNotBlank() },
                            endTime = endTime.takeIf { it.isNotBlank() },
                            dueDate = dueDate.takeIf { it.isNotBlank() }
                        ),
                        onComplete = onCreate
                    )
                },
                enabled = title.isNotBlank() && actionState !is ResourceState.Loading,
                text = if (actionState is ResourceState.Loading) "Creating..." else "Create Ticket"
            )

            Spacer(Modifier.height(16.dp))
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
        modifier = Modifier.fillMaxWidth().clickable { onTicketClick(ticket) }
            .pointerHoverIcon(PointerIcon.Hand),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(borderColor))
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Text(
                    text = ticket.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Priority: ${ticket.priority ?: "medium"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = "Status: ${ticket.status}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(6.dp))

                ticket.dueDate?.let { dueDate ->
                    Text(
                        text = "Due: $dueDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(6.dp))
                }

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
                        text = UserManager.getUserName(ticket.assignedTo),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                onMove?.let {
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = { it(ticket) },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
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

fun extractMentionedUserIds(text: String): List<String> {
    return Regex("@\\{(.*?)\\}")
        .findAll(text)
        .map { it.groupValues[1] }
        .toList()
}

fun stripHtml(html: String): String {
    return html
        .replace(Regex("(?i)<br\\s*/?>"), " ")
        .replace(Regex("(?i)</p>"), " ")
        .replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

fun insertMention(user: User, state: RichTextState) {
    val currentHtml = state.toHtml()
    val plain = stripHtml(currentHtml)
    val words = plain.split(" ").toMutableList()

    if (words.isNotEmpty()) {
        words[words.lastIndex] = "@${UserManager.getUserName(user.id)}"
    } else {
        words.add("@${UserManager.getUserName(user.id)}")
    }

    val newText = words.joinToString(" ")
    state.setHtml("<p>$newText</p>")
}


fun htmlToPlainText(html: String): String {
    return html
        .replace("&commat;", "@")
        .replace("&nbsp;", " ")
        .replace(Regex("<[^>]*>"), "") // remove tags
        .trim()
}

fun cleanEmailMessage(html: String): String {
    return html
        .replace("&commat;", "@")
        .replace("&nbsp;", " ")
        .replace(Regex("<[^>]*>"), "") // remove HTML tags
        .trim()
}

@Composable
fun JiraCommentBoxV2(
    ticket: Ticket,
    users: List<User>,
    onSend: (String) -> Unit
) {
    val state = rememberRichTextState()
    var isExpanded by remember { mutableStateOf(false) }

    var mentionQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<User>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val selectedMentions = remember { mutableStateListOf<User>() }

    LaunchedEffect(Unit) {
        snapshotFlow { state.annotatedString.text }
            .collect { text ->

                println("TEXT: $text")

                val lastWord = text.substringAfterLast(" ").trim()

                println("LAST WORD: $lastWord")

                if (lastWord.startsWith("@")) {
                    mentionQuery = lastWord.removePrefix("@")

                    println("ALL_USERS: ${users}")
                    suggestions = users
                        .filter {
                            it.name.contains(mentionQuery, ignoreCase = true)
                        }
                        .take(5)

                    showSuggestions = suggestions.isNotEmpty()

                    println("SUGGESTIONS: ${suggestions.map { it.name }}")
                } else {
                    showSuggestions = false
                }
            }
    }


    Box(modifier = Modifier.fillMaxWidth()) {
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
            if (!isExpanded) {
                Text(
                    "Add a comment...",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = true }
                        .pointerHoverIcon(PointerIcon.Hand)
                        .padding(8.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.Default.FormatBold, "Bold")
                    }
                    IconButton(
                        onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.Default.FormatItalic, "Italic")
                    }
                    IconButton(
                        onClick = { state.toggleCodeSpan() },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.Default.Code, "Code")
                    }
                    IconButton(
                        onClick = { state.toggleUnorderedList() },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.FormatListBulleted, "List")
                    }
                }

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
                    if (state.toHtml().isEmpty() || stripHtml(state.toHtml()).isEmpty()) {
                        Text("Write a comment...", color = Color.Gray)
                    }
                    RichTextEditor(
                        state = state,
                        modifier = Modifier.fillMaxSize().pointerHoverIcon(PointerIcon.Text)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            isExpanded = false
                            state.setHtml("")
                        },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val html = state.toHtml()
                            if (html.isBlank()) return@Button
                            // val mentionedIds = extractMentionedUserIds(html)
                            val filteredMentioned = selectedMentions.filter { itemUser ->
                                htmlToPlainText(html).contains("@${itemUser.name}")
                            }
                            println("filtered mentioned ids : $filteredMentioned")
                            val validIds = filteredMentioned.map { it.id }
                            println("valid mentioned ids : $validIds")
                            scope.launch {
                                NotificationHelper.notifyMentionedUsers(
                                    validIds,
                                    ticket,
                                    cleanEmailMessage(html)
                                )
                            }
                            onSend(html)
                            state.setHtml("")
                            isExpanded = false
                        },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                    ) {
                        Text("Comment")
                    }
                }
            }
        }

        if (showSuggestions) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { showSuggestions = false },
                offset = IntOffset(0, 100),
                properties = PopupProperties(focusable = false)
            ) {
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column {
                        suggestions.forEach { user ->
                            Text(
                                text = user.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMentions.add(user)
                                        insertMention(user, state)
                                        println("selected mention size: ${selectedMentions.size}")
                                        showSuggestions = false
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyValueRowItem(key: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            "$key : ",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            color = Color.Gray, modifier = Modifier.weight(.5f)
        )

        Text(
            value,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Normal,
            color = Color.Gray, modifier = Modifier.weight(.5f)
        )
    }
}

/*** In V4 we have updated Ticket Details UI Completely by adding rich text editor **/
@Composable
fun TicketDetailScreenV4(
    ticket: Ticket,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
    onClickEditTicket: (Ticket) -> Unit
) {
    val commentsState by viewModel.commentsState.collectAsState()
    val historyState by viewModel.historyState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(ticket.id) {
        viewModel.loadComments(ticket.id.toString())
        viewModel.loadUsers() // Load users for mentions
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

                            IconButton(
                                onClick = { scope.launch { drawerState.close() } },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider()

                        // 🔹 CONTENT
                        ResourceHandler(
                            state = historyState,
                            onLoading = {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    items(5) {
                                        HistoryShimmerItem()
                                    }
                                }
                            }
                        ) { historyItems ->
                            if (historyItems.isEmpty()) {
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
                                    items(historyItems) { item ->
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


                                                // for description, we are storing contents in html format as we are using Rich Text Editor.
                                                // So for that we have to parse it and show like normal text
                                                if (item.fieldName.lowercase()
                                                        .contains("description")
                                                ) {
                                                    Text(
                                                        "${htmlToPlainText(item.oldValue ?: "")} ->  ${
                                                            htmlToPlainText(
                                                                item.newValue ?: ""
                                                            )
                                                        }",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                } else {
                                                    Text(
                                                        "${item.oldValue} ->  ${item.newValue}",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }



                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    "By ${UserManager.getUserName(item.changedBy)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )

                                                Text(
                                                    "On ${Utils.formatDateTime(item.changedAt ?: "")}",
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
                        onLogout = onLogout,
                        onSearch = onSearchClick
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
                            Text(
                                text = "${ticket.ticketCode}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,

                                )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
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
                                    if (UserManager.isAdmin(AuthManager.userId) || UserManager.isSuperAdmin(
                                            AuthManager.userId
                                        )
                                    ) {
                                        IconButton(
                                            onClick = { onClickEditTicket(ticket) },
                                            modifier = Modifier.weight(.5f)
                                                .pointerHoverIcon(PointerIcon.Hand)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }


                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                viewModel.loadTicketHistory(ticket.id.toString())
                                                drawerState.open()
                                            }
                                        },
                                        modifier = Modifier.weight(.5f)
                                            .pointerHoverIcon(PointerIcon.Hand)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            val descState = rememberRichTextState()
                            LaunchedEffect(ticket.description) {
                                descState.setHtml(ticket.description)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(11.dp)
                                    ).padding(10.dp)
                            ) {
                                RichText(
                                    state = descState,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        item {

                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Details",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(10.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(11.dp)
                                    ).padding(10.dp)
                            ) {

                                KeyValueRowItem(key = "Priority", value = ticket.priority ?: "")
                                KeyValueRowItem(
                                    key = "Due Date",
                                    value = Utils.formatDateTime(ticket.dueDate ?: "")
                                )
                                KeyValueRowItem(
                                    key = "Assigned To",
                                    value = UserManager.getUserName(
                                        ticket.assignedTo
                                    )
                                )
                            }


                        }

                        item {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Add Comment",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(10.dp))
                            val users = UserManager.getAllUsers()
                            JiraCommentBoxV2(ticket, users) { htmlComment ->
                                viewModel.addCommentToTicket(
                                    ticket.id.toString(),
                                    htmlComment,
                                    parentId = ""
                                )
                            }
                        }

                        item {
                            Column(modifier = Modifier.weight(1f)) {
                                Spacer(Modifier.height(20.dp))
                                Text(
                                    "Comments",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(10.dp))

                                ResourceHandler(
                                    state = commentsState,
                                    onLoading = {
                                        Spacer(Modifier.height(20.dp))
                                        Column {
                                            repeat(3) {
                                                CommentShimmerItem()
                                            }
                                        }
                                    }
                                ) { comments ->
                                    val parentComments = comments.filter { it.parentId == null }
                                    fun getReplies(parentId: String): List<Comment> {
                                        return comments.filter { it.parentId == parentId }
                                    }
                                    parentComments.forEach { parentCommentItem ->
                                        CommentItem(parentCommentItem, ticket = ticket)
                                        val replies = getReplies(parentCommentItem.id ?: "")
                                        if (replies.isNotEmpty()) {
                                            replies.forEach { reply ->
                                                ReplyItemForComment(reply)
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
    }
}

/*** In V5 we have updated routing feature web  **/
@Composable
fun TicketDetailScreenV5(
    ticketCode: String,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
    onClickEditTicket: (Ticket) -> Unit
) {
    val commentsState by viewModel.commentsState.collectAsState()
    val historyState by viewModel.historyState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val fetchTicketState by viewModel.ticketByTicketCodeState.collectAsState()

    LaunchedEffect(ticketCode) {
        viewModel.fetchTicketByTicketCode(ticketCode)
        viewModel.loadUsers()
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

                            IconButton(
                                onClick = { scope.launch { drawerState.close() } },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider()

                        // 🔹 CONTENT
                        ResourceHandler(
                            state = historyState,
                            onLoading = {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    items(5) {
                                        HistoryShimmerItem()
                                    }
                                }
                            }
                        ) { historyItems ->
                            if (historyItems.isEmpty()) {
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
                                    items(historyItems) { item ->
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


                                                // for description, we are storing contents in html format as we are using Rich Text Editor.
                                                // So for that we have to parse it and show like normal text
                                                if (item.fieldName.lowercase()
                                                        .contains("description")
                                                ) {
                                                    Text(
                                                        "${htmlToPlainText(item.oldValue ?: "")} ->  ${
                                                            htmlToPlainText(
                                                                item.newValue ?: ""
                                                            )
                                                        }",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                } else {
                                                    Text(
                                                        "${item.oldValue} ->  ${item.newValue}",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }



                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    "By ${UserManager.getUserName(item.changedBy)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )

                                                Text(
                                                    "On ${Utils.formatDateTime(item.changedAt ?: "")}",
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
            ResourceHandler(
                state = fetchTicketState,
                onLoading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            ) { ticket ->
                ticket?.let {
                    LaunchedEffect(ticket.id) {
                        viewModel.loadComments(ticket.id.toString())
                    }

                    // YOUR EXISTING SCREEN CONTENT HERE
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        // Basic detail view for now
                        Column(Modifier.fillMaxSize()) {
                            CommonTopBar(
                                title = "Ticket Details",
                                showBack = true,
                                onBack = onBack,
                                onLogout = onLogout,
                                onSearch = onSearchClick
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
                                    Text(
                                        text = "${ticket.ticketCode}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,

                                        )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
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
                                            if (UserManager.isAdmin(AuthManager.userId) || UserManager.isSuperAdmin(
                                                    AuthManager.userId
                                                )
                                            ) {
                                                IconButton(
                                                    onClick = { onClickEditTicket(ticket) },
                                                    modifier = Modifier.weight(.5f)
                                                        .pointerHoverIcon(PointerIcon.Hand)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit",
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }


                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        viewModel.loadTicketHistory(ticket.id.toString())
                                                        drawerState.open()
                                                    }
                                                },
                                                modifier = Modifier.weight(.5f)
                                                    .pointerHoverIcon(PointerIcon.Hand)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = "Profile",
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }

                                    val descState = rememberRichTextState()
                                    LaunchedEffect(ticket.description) {
                                        descState.setHtml(ticket.description)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(11.dp)
                                            ).padding(10.dp)
                                    ) {
                                        RichText(
                                            state = descState,
                                            modifier = Modifier.fillMaxWidth(),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                item {

                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        "Details",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(10.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(11.dp)
                                            ).padding(10.dp)
                                    ) {

                                        KeyValueRowItem(
                                            key = "Priority",
                                            value = ticket.priority ?: ""
                                        )
                                        KeyValueRowItem(
                                            key = "Due Date",
                                            value = Utils.formatDateTime(ticket.dueDate ?: "")
                                        )
                                        KeyValueRowItem(
                                            key = "Assigned To",
                                            value = UserManager.getUserName(
                                                ticket.assignedTo
                                            )
                                        )
                                    }


                                }

                                item {
                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        "Add Comment",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    val users = UserManager.getAllUsers()
                                    JiraCommentBoxV2(ticket, users) { htmlComment ->
                                        viewModel.addCommentToTicket(
                                            ticket.id.toString(),
                                            htmlComment,
                                            parentId = ""
                                        )
                                    }
                                }

                                item {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Spacer(Modifier.height(20.dp))
                                        Text(
                                            "Comments",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.height(10.dp))

                                        ResourceHandler(
                                            state = commentsState,
                                            onLoading = {
                                                Spacer(Modifier.height(20.dp))
                                                Column {
                                                    repeat(3) {
                                                        CommentShimmerItem()
                                                    }
                                                }
                                            }
                                        ) { comments ->
                                            val parentComments =
                                                comments.filter { it.parentId == null }

                                            fun getReplies(parentId: String): List<Comment> {
                                                return comments.filter { it.parentId == parentId }
                                            }
                                            parentComments.forEach { parentCommentItem ->
                                                CommentItem(parentCommentItem, ticket = ticket)
                                                val replies = getReplies(parentCommentItem.id ?: "")
                                                if (replies.isNotEmpty()) {
                                                    replies.forEach { reply ->
                                                        ReplyItemForComment(reply)
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


            }

        }
    }
}

/*** In V6 we are restructuring layouts for both Mobile & Desktop Screens  **/
@Composable
fun TicketDetailScreenV6(
    ticketCode: String,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
    onClickEditTicket: (Ticket) -> Unit
) {
    val commentsState by viewModel.commentsState.collectAsState()
    val historyState by viewModel.historyState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val fetchTicketState by viewModel.ticketByTicketCodeState.collectAsState()
    val isTabletOrDesktop = true

    LaunchedEffect(ticketCode) {
        viewModel.fetchTicketByTicketCode(ticketCode)
        viewModel.loadUsers()
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

                            IconButton(
                                onClick = { scope.launch { drawerState.close() } },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider()

                        // 🔹 CONTENT
                        ResourceHandler(
                            state = historyState,
                            onLoading = {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    items(5) {
                                        HistoryShimmerItem()
                                    }
                                }
                            }
                        ) { historyItems ->
                            if (historyItems.isEmpty()) {
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
                                    items(historyItems) { item ->
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


                                                // for description, we are storing contents in html format as we are using Rich Text Editor.
                                                // So for that we have to parse it and show like normal text
                                                if (item.fieldName.lowercase()
                                                        .contains("description")
                                                ) {
                                                    Text(
                                                        "${htmlToPlainText(item.oldValue ?: "")} ->  ${
                                                            htmlToPlainText(
                                                                item.newValue ?: ""
                                                            )
                                                        }",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                } else {
                                                    Text(
                                                        "${item.oldValue} ->  ${item.newValue}",
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }



                                                Spacer(Modifier.height(4.dp))

                                                Text(
                                                    "By ${UserManager.getUserName(item.changedBy)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )

                                                Text(
                                                    "On ${Utils.formatDateTime(item.changedAt ?: "")}",
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
            ResourceHandler(
                state = fetchTicketState,
                onLoading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            ) { ticket ->
                ticket?.let {
                    LaunchedEffect(ticket.id) {
                        viewModel.loadComments(ticket.id.toString())
                    }

                    // YOUR EXISTING SCREEN CONTENT HERE
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        // Basic detail view for now
                        Column(Modifier.fillMaxSize()) {
                            CommonTopBar(
                                title = "Ticket Details",
                                showBack = true,
                                onBack = onBack,
                                onLogout = onLogout,
                                onSearch = onSearchClick
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
                                    Text(
                                        text = "${ticket.ticketCode}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,

                                        )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
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
                                            if (UserManager.isAdmin(AuthManager.userId) || UserManager.isSuperAdmin(
                                                    AuthManager.userId
                                                )
                                            ) {
                                                IconButton(
                                                    onClick = { onClickEditTicket(ticket) },
                                                    modifier = Modifier.weight(.5f)
                                                        .pointerHoverIcon(PointerIcon.Hand)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit",
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }


                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        viewModel.loadTicketHistory(ticket.id.toString())
                                                        drawerState.open()
                                                    }
                                                },
                                                modifier = Modifier.weight(.5f)
                                                    .pointerHoverIcon(PointerIcon.Hand)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.History,
                                                    contentDescription = "Profile",
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }

                                    val descState = rememberRichTextState()
                                    LaunchedEffect(ticket.description) {
                                        descState.setHtml(ticket.description)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(11.dp)
                                            ).padding(10.dp)
                                    ) {
                                        RichText(
                                            state = descState,
                                            modifier = Modifier.fillMaxWidth(),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                item {

                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        "Details",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(10.dp))

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(11.dp)
                                            ).padding(10.dp)
                                    ) {

                                        KeyValueRowItem(
                                            key = "Priority",
                                            value = ticket.priority ?: ""
                                        )
                                        KeyValueRowItem(
                                            key = "Due Date",
                                            value = Utils.formatDateTime(ticket.dueDate ?: "")
                                        )
                                        KeyValueRowItem(
                                            key = "Assigned To",
                                            value = UserManager.getUserName(
                                                ticket.assignedTo
                                            )
                                        )
                                    }


                                }

                                item {
                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        "Add Comment",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    val users = UserManager.getAllUsers()
                                    JiraCommentBoxV2(ticket, users) { htmlComment ->
                                        viewModel.addCommentToTicket(
                                            ticket.id.toString(),
                                            htmlComment,
                                            parentId = ""
                                        )
                                    }
                                }

                                item {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Spacer(Modifier.height(20.dp))
                                        Text(
                                            "Comments",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.height(10.dp))

                                        ResourceHandler(
                                            state = commentsState,
                                            onLoading = {
                                                Spacer(Modifier.height(20.dp))
                                                Column {
                                                    repeat(3) {
                                                        CommentShimmerItem()
                                                    }
                                                }
                                            }
                                        ) { comments ->
                                            val parentComments =
                                                comments.filter { it.parentId == null }

                                            fun getReplies(parentId: String): List<Comment> {
                                                return comments.filter { it.parentId == parentId }
                                            }
                                            parentComments.forEach { parentCommentItem ->
                                                CommentItem(parentCommentItem, ticket = ticket)
                                                val replies = getReplies(parentCommentItem.id ?: "")
                                                if (replies.isNotEmpty()) {
                                                    replies.forEach { reply ->
                                                        ReplyItemForComment(reply)
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


            }

        }
    }
}

/*** In V6 we are restructuring layouts for both Mobile & Desktop Screens  **/
@Composable
fun TicketDetailScreenV7(
    ticketCode: String,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
    onClickEditTicket: (Ticket) -> Unit
) {
    val historyState by viewModel.historyState.collectAsState()
    val usersState by viewModel.usersState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val fetchTicketState by viewModel.ticketByTicketCodeState.collectAsState()
    val isTabletOrDesktop = true

    LaunchedEffect(ticketCode) {
        viewModel.fetchTicketByTicketCode(ticketCode)
        viewModel.loadUsers()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false,
            drawerContent = {
                DrawerContent(scope, historyState, drawerState)
            }
        ) {
            ResourceHandler(
                state = fetchTicketState,
                onLoading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            ) { ticket ->

                // YOUR EXISTING SCREEN CONTENT HERE
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    // Basic detail view for now
                    Column(Modifier.fillMaxSize()) {
                        CommonTopBar(
                            title = "Ticket Details",
                            showBack = true,
                            onBack = onBack,
                            onLogout = onLogout,
                            onSearch = onSearchClick
                        )
                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(11.dp))

                        TicketDetailsUI(
                            ticket,
                            onTicketEditClick = onClickEditTicket,
                            scope = scope,
                            drawerState = drawerState
                        )


                    }
                }


            }

        }
    }
}

@Composable
fun TicketDetailsUI(
    ticket: Ticket?,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onTicketEditClick: (Ticket) -> Unit,
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    val commentsState by viewModel.commentsState.collectAsState()
    var isTabletOrDesktop = true
    val posture = calculateDevicePosture()

    when (posture) {
        DevicePosture.Desktop -> {
            isTabletOrDesktop = true
        }

        DevicePosture.Tablet -> {
            isTabletOrDesktop = true
        }

        DevicePosture.Mobile -> {
            isTabletOrDesktop = false
        }
    }

    ticket?.let {
        LaunchedEffect(ticket.id) {
            viewModel.loadComments(ticket.id.toString())
        }

        if (isTabletOrDesktop) {
            Row(modifier = Modifier.fillMaxWidth().absolutePadding(left = 11.dp, right = 11.dp)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .weight(.6f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // 🔹 Ticket Info Card
                    item {
                        JiraCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    text = ticket.ticketCode ?: "",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = ticket.title,
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                Spacer(Modifier.height(12.dp))

                                val descState = rememberRichTextState()
                                LaunchedEffect(ticket.description) {
                                    descState.setHtml(ticket.description)
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(12.dp)
                                ) {
                                    RichText(
                                        state = descState,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // 🔹 Add Comment Card
                    item {
                        JiraCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    "Add Comment",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.height(12.dp))

                                val users = UserManager.getAllUsers()

                                JiraCommentBoxV2(ticket, users) { htmlComment ->
                                    viewModel.addCommentToTicket(
                                        ticket.id.toString(),
                                        htmlComment,
                                        parentId = ""
                                    )
                                }
                            }
                        }
                    }

                    // 🔹 Comments Card
                    item {
                        JiraCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text(
                                    "Comments",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(Modifier.height(12.dp))

                                ResourceHandler(
                                    state = commentsState,
                                    onLoading = {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            repeat(3) { CommentShimmerItem() }
                                        }
                                    }
                                ) { comments ->

                                    val parentComments = comments.filter { it.parentId == null }

                                    fun getReplies(parentId: String): List<Comment> {
                                        return comments.filter { it.parentId == parentId }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                        parentComments.forEach { parent ->

                                            CommentItem(parent, ticket = ticket)

                                            val replies = getReplies(parent.id ?: "")

                                            if (replies.isNotEmpty()) {
                                                Column(
                                                    modifier = Modifier.padding(start = 16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    replies.forEach { reply ->
                                                        ReplyItemForComment(reply)
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


                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(.4f)) {
                    TicketDetailsListComponent(
                        ticket,
                        onTicketEditClick,
                        onclickLoadTicketHistory = {
                            scope.launch {
                                viewModel.loadTicketHistory(ticket.id.toString())
                                drawerState.open()
                            }
                        })


                }

            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 🔹 Ticket Info Card
                item {
                    JiraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = ticket.ticketCode ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = ticket.title,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(Modifier.height(12.dp))

                            val descState = rememberRichTextState()
                            LaunchedEffect(ticket.description) {
                                descState.setHtml(ticket.description)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                RichText(
                                    state = descState,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    TicketDetailsListComponent(
                        ticket,
                        onTicketEditClick,
                        onclickLoadTicketHistory = {
                            scope.launch {
                                viewModel.loadTicketHistory(ticket.id.toString())
                                drawerState.open()
                            }
                        })
                }

                // 🔹 Add Comment Card
                item {
                    JiraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Add Comment",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(12.dp))

                            val users = UserManager.getAllUsers()

                            JiraCommentBoxV2(ticket, users) { htmlComment ->
                                viewModel.addCommentToTicket(
                                    ticket.id.toString(),
                                    htmlComment,
                                    parentId = ""
                                )
                            }
                        }
                    }
                }

                // 🔹 Comments Card
                item {
                    JiraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Comments",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(12.dp))

                            ResourceHandler(
                                state = commentsState,
                                onLoading = {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        repeat(3) { CommentShimmerItem() }
                                    }
                                }
                            ) { comments ->

                                val parentComments = comments.filter { it.parentId == null }

                                fun getReplies(parentId: String): List<Comment> {
                                    return comments.filter { it.parentId == parentId }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                    parentComments.forEach { parent ->

                                        CommentItem(parent, ticket = ticket)

                                        val replies = getReplies(parent.id ?: "")

                                        if (replies.isNotEmpty()) {
                                            Column(
                                                modifier = Modifier.padding(start = 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                replies.forEach { reply ->
                                                    ReplyItemForComment(reply)
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


        }
    }


}


@Composable
fun ActionIcon(
    icon: ImageVector,
    bgColor: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.12f))
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = bgColor,
            modifier = Modifier.size(18.dp)
        )
    }
}
@Composable
fun TicketTypeDisplay(type: String?) {

    val ticketType = remember(type) {
        TicketType.entries.find { it.name == type } ?: TicketType.TASK
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = getTypeColor(ticketType).copy(alpha = 0.12f),
        border = BorderStroke(
            1.dp,
            getTypeColor(ticketType).copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = getTypeIcon(ticketType),
                contentDescription = null,
                tint = getTypeColor(ticketType),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = ticketType.label,
                color = getTypeColor(ticketType),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun TicketTypeChips(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = TicketType.entries.toTypedArray()

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        types.forEach { type ->
            val isSelected = type.name == selectedType

            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type.name) },
                label = { Text(type.label) },
                leadingIcon = {
                    Icon(
                        getTypeIcon(type),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getTypeColor(type).copy(alpha = 0.2f),
                    selectedLabelColor = getTypeColor(type),
                    selectedLeadingIconColor = getTypeColor(type)
                )
            )
        }
    }
}

fun getTypeIcon(type: TicketType): ImageVector {
    return when (type) {
        TicketType.BUG -> Icons.Default.BugReport
        TicketType.FEATURE -> Icons.Default.Star
        TicketType.IMPROVEMENT -> Icons.Default.TrendingUp
        TicketType.TASK -> Icons.Default.CheckCircle
    }
}

fun getTypeColor(type: TicketType): Color {
    return when (type) {
        TicketType.BUG -> Color.Red
        TicketType.FEATURE -> Color(0xFF1976D2)
        TicketType.IMPROVEMENT -> Color(0xFF7B1FA2)
        TicketType.TASK -> Color(0xFF388E3C)
    }
}


@Composable
fun TicketDetailsListComponent(
    ticket: Ticket,
    onClickEditTicket: (Ticket) -> Unit,
    onclickLoadTicketHistory: (String) -> Unit,
    viewModel: TicketViewModel = viewModel { TicketViewModel() }
) {


    val ticketTypeList = listOf(
        "Bug",
        "Feature",
        "Improvement",
        "Task"
    )

    JiraCard() {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            // 🔷 HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "DETAILS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        "Ticket Info",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    if (UserManager.isAdmin(AuthManager.userId) ||
                        UserManager.isSuperAdmin(AuthManager.userId)
                    ) {
                        ActionIcon(
                            icon = Icons.Default.Edit,
                            bgColor = MaterialTheme.colorScheme.primary,
                            onClick = { onClickEditTicket(ticket) }
                        )
                    }

                    ActionIcon(
                        icon = Icons.Default.History,
                        bgColor = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            onclickLoadTicketHistory(ticket.id.toString())
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Divider(color = MaterialTheme.colorScheme.outline.copy(0.15f))

            Spacer(Modifier.height(16.dp))

            // 🔷 DETAILS CONTENT
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

               // TicketTypeChips(selectedType = TicketType.BUG.name, onTypeSelected = {})
                TicketTypeDisplay(TicketType.BUG.name)

                DetailItem(
                    icon = Icons.Default.Flag,
                    label = "Priority",
                    valueComposable = {
                        PriorityBadge(ticket.priority ?: "Not set")
                    }
                )

                DetailItem(
                    icon = Icons.Default.DateRange,
                    label = "Due Date",
                    value = Utils.formatDateTime(ticket.dueDate ?: "")
                )

                DetailItem(
                    icon = Icons.Default.Person,
                    label = "Assigned To",
                    value = UserManager.getUserName(ticket.assignedTo)
                )

                DetailItem(
                    icon = Icons.Default.Person,
                    label = "Created By",
                    value = UserManager.getUserName(ticket.createdBy)
                )

                DetailItem(
                    icon = Icons.Default.Assignment,
                    label = "Status",
                    valueComposable = {
                        PriorityBadge(ticket.status ?: "Not set")
                    }
                )
                MoveToStatus(
                    currentStatus = ticket.status ?: "",
                    onStatusSelected = { newStatus ->
                        viewModel.updateTicketStatus(ticket, newStatus)
                    }
                )
            }
        }
    }
}

@Composable
fun MoveToStatus(
    currentStatus: String,
    onStatusSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val statusList = listOf(
        "TODO",
        "IN_PROGRESS",
        "QA",
        "DONE"
    )

    Box {
        // 🔘 Button UI
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { expanded = true }
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.SyncAlt,
                contentDescription = "Move To",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "Move To",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )

            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // 📌 Dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusList.forEach { status ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (status == currentStatus) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(status.replace("_", " "))
                        }
                    },
                    onClick = {
                        expanded = false
                        onStatusSelected(status)
                    }
                )
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val color = when (priority.lowercase()) {
        "high" -> Color.Red
        "medium" -> Color(0xFFFFA500)
        "low" -> Color.Green
        else -> Color.Gray
    }

    Text(
        text = priority,
        color = color,
        modifier = Modifier
            .background(color.copy(.1f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String? = null,
    valueComposable: (@Composable () -> Unit)? = null
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 Icon Bubble
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(0.08f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(2.dp))

            if (valueComposable != null) {
                valueComposable()
            } else {
                Text(
                    value ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    historyState: ResourceState<List<TicketHistory>>,
    drawerState: DrawerState,

    ) {
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

                IconButton(
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider()

            // 🔹 CONTENT
            ResourceHandler(
                state = historyState,
                onLoading = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(5) {
                            HistoryShimmerItem()
                        }
                    }
                }
            ) { historyItems ->
                if (historyItems.isEmpty()) {
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
                        items(historyItems) { item ->
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


                                    // for description, we are storing contents in html format as we are using Rich Text Editor.
                                    // So for that we have to parse it and show like normal text
                                    if (item.fieldName.lowercase()
                                            .contains("description")
                                    ) {
                                        Text(
                                            "${htmlToPlainText(item.oldValue ?: "")} ->  ${
                                                htmlToPlainText(
                                                    item.newValue ?: ""
                                                )
                                            }",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    } else {
                                        Text(
                                            "${item.oldValue} ->  ${item.newValue}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }



                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        "By ${UserManager.getUserName(item.changedBy)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )

                                    Text(
                                        "On ${Utils.formatDateTime(item.changedAt ?: "")}",
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

@Composable
fun CommentItem(
    comment: Comment,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    ticket: Ticket
) {
    val replyingToCommentId = remember { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
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
                text = UserManager.getUserName(comment.createdBy)
                    .take(1).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // 🔹 Name + Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    UserManager.getUserName(comment.createdBy),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "On ${
                        Utils.formatDateTime(
                            comment.createdAt ?: ""
                        )
                    }",
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

            Text(
                "Reply",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.clickable {
                    replyingToCommentId.value = comment.id ?: ""
                },
                color = Color.Gray
            )

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.4f)
            )

            if (replyingToCommentId.value == comment.id) {
                JiraCommentBoxV2(ticket, UserManager.getAllUsers()) { htmlComment ->
                    viewModel.addCommentToTicket(
                        ticket.id.toString(),
                        htmlComment,
                        parentId = comment.id
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(12.dp))
}

fun Modifier.leftBorder(
    width: Dp = 1.dp,
    color: Color = Color.LightGray,
    paddingStart: Dp = 8.dp
): Modifier = this
    .padding(start = paddingStart)
    .drawBehind {
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(width.toPx(), size.height),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }

@Composable
fun ReplyItemForComment(comment: Comment) {
    Row(
        modifier = Modifier.fillMaxWidth().absolutePadding(left = 40.dp).leftBorder(),
        verticalAlignment = Alignment.Top
    ) {

        Spacer(modifier = Modifier.width(10.dp))
        // 🔹 Avatar
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = UserManager.getUserName(comment.createdBy)
                    .take(1).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // 🔹 Name + Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    UserManager.getUserName(comment.createdBy),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "On ${
                        Utils.formatDateTime(
                            comment.createdAt ?: ""
                        )
                    }",
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

            /* HorizontalDivider(
                 color = Color.LightGray.copy(alpha = 0.4f)
             )*/
        }
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
fun StatusChips(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit
) {
    val statuses = listOf(
        null to "All",
        "todo" to "Todo",
        "in_progress" to "In Progress",
        "on_qa" to "QA",
        "on_hold" to "On Hold",
        "closed" to "Closed"
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(statuses) { (value, label) ->

            val isSelected = selectedStatus == value

            FilterChip(
                selected = isSelected,
                onClick = { onStatusSelected(value) },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color.DarkGray
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getStatusColor(value),
                    containerColor = Color(0xFFF1F1F1)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) Color.Transparent else Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        }
    }
}

fun getStatusColor(status: String?): Color {
    return when (status) {
        "todo" -> Color.Gray
        "in_progress" -> Color(0xFF1976D2) // blue
        "on_qa" -> Color(0xFF7B1FA2) // purple
        "on_hold" -> Color(0xFFA88915) // purple
        "closed" -> Color(0xFF2E7D32) // green
        else -> Color.Black
    }
}

@Composable
fun TaskListScreen(
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onTicketClick: (Ticket) -> Unit
) {
    val allTicketsState by viewModel.allTicketsState.collectAsState()
    var selectedUser by remember {
        mutableStateOf<User?>(
            UserManager.getUser(
                AuthManager.userId ?: ""
            )
        )
    }
    val usersState by viewModel.usersState.collectAsState()

    var selectedStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    LaunchedEffect(selectedUser) {
        viewModel.getAllTicketsByUserId(selectedUser?.id ?: "")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        /*  Text("All My Tasks", style = MaterialTheme.typography.headlineSmall)
          Spacer(Modifier.height(16.dp))*/

        ResourceHandler(
            state = usersState,
            onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        ) { users ->
            UserSearchView(
                users = users,
                selectedUser = selectedUser,
                onUserSelected = { selectedUser = it }
            )
        }


        StatusChips(selectedStatus = selectedStatus, onStatusSelected = { status ->
            selectedStatus = status
        })


        Spacer(modifier = Modifier.height(20.dp))

        ResourceHandler(
            state = allTicketsState,
            onLoading = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(5) {
                        TicketShimmerItem()
                    }
                }
            }
        ) { tickets ->
            val filteredTickets = tickets.filter { ticket ->
                val matchesUser = selectedUser == null || ticket.assignedTo == selectedUser?.id
                val matchesStatus = selectedStatus == null || ticket.status == selectedStatus

                matchesUser && matchesStatus
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredTickets) { ticket ->
                    TicketCardV2(
                        ticket = ticket,
                        onTicketClick = onTicketClick,
                        onMove = null
                    )
                }
            }
        }
    }
}

@Composable
fun EditTicketScreen(
    ticket: Ticket,
    onTicketUpdated: (Ticket) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
) {
    val usersState by viewModel.usersState.collectAsState()
    val updateState by viewModel.updateTicketState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    var title by remember { mutableStateOf(ticket.title) }
    var status by remember { mutableStateOf(ticket.status) }
    var priority by remember { mutableStateOf(ticket.priority ?: "medium") }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var startTime by remember { mutableStateOf(ticket.startTime ?: "") }
    var endTime by remember { mutableStateOf(ticket.endTime ?: "") }
    var dueDate by remember { mutableStateOf(ticket.dueDate ?: "") }
    val descState = rememberRichTextState()
    val scrollState = rememberScrollState()

    LaunchedEffect(ticket.description) {
        descState.setHtml(ticket.description)
    }

    // Attempt to match selected user
    LaunchedEffect(usersState) {
        if (usersState is ResourceState.Success) {
            val users = (usersState as ResourceState.Success<List<User>>).data
            selectedUser = users.find { it.name == ticket.assignedTo }
        }
    }

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(
            title = "Edit Ticket",
            showBack = true,
            onBack = { /* Need to navigate back properly if injected but assumed standard */ },
            onSearch = onSearchClick
        )

        Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {

            Text("Edit Ticket", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            JiraTextField(
                value = title,
                onValueChange = { title = it },
                label = "Title",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Description",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            JiraRichTextEditor(
                state = descState,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            SimpleDropdown(
                label = "Status",
                options = listOf(Status.TODO.value, Status.IN_PROGRESS.value, Status.DONE.value),
                selectedOption = status,
                onOptionSelected = { status = it }
            )

            Spacer(Modifier.height(16.dp))

            SimpleDropdown(
                label = "Priority",
                options = listOf("low", "medium", "high", "critical"),
                selectedOption = priority,
                onOptionSelected = { priority = it }
            )

            Spacer(Modifier.height(16.dp))

            ResourceHandler(
                state = usersState,
                onLoading = { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            ) { users ->
                UserDropdown(
                    users = users,
                    selectedUser = selectedUser,
                    onUserSelected = { selectedUser = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            DatePickerField(
                label = "Start Time (YYYY-MM-DD)",
                selectedDate = startTime,
                onDateSelected = { startTime = it }
            )

            Spacer(Modifier.height(16.dp))

            DatePickerField(
                label = "End Time (YYYY-MM-DD)",
                selectedDate = endTime,
                onDateSelected = { endTime = it }
            )

            Spacer(Modifier.height(16.dp))

            DatePickerField(
                label = "Due Date (YYYY-MM-DD)",
                selectedDate = dueDate,
                onDateSelected = { dueDate = it }
            )

            Spacer(Modifier.height(32.dp))

            MainButton(
                onClick = {
                    viewModel.updateTicket(
                        oldTicket = ticket,
                        title = title,
                        description = descState.toHtml(),
                        newStatus = status,
                        priority = priority,
                        selectedUser = selectedUser,
                        startTime = startTime.takeIf { it.isNotBlank() },
                        endTime = endTime.takeIf { it.isNotBlank() },
                        dueDate = dueDate.takeIf { it.isNotBlank() },
                        projectId = ticket.projectId ?: "",
                        onTicketUpdated = onTicketUpdated
                    )
                },
                enabled = title.isNotBlank() && updateState !is ResourceState.Loading,
                text = if (updateState is ResourceState.Loading) "Saving..." else "Save Changes"
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}
