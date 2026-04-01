package com.atu.jira.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.auth.AuthManager
import com.atu.jira.components.CommonTopBar
import com.atu.jira.components.DevicePosture
import com.atu.jira.components.JiraButton
import com.atu.jira.components.JiraButtonWithLoader
import com.atu.jira.components.JiraCard
import com.atu.jira.components.JiraTextField
import com.atu.jira.components.LoadingUI
import com.atu.jira.components.MainButton
import com.atu.jira.components.ResourceHandler
import com.atu.jira.components.calculateDevicePosture
import com.atu.jira.model.Project
import com.atu.jira.model.Status
import com.atu.jira.model.Ticket
import com.atu.jira.model.TicketType
import com.atu.jira.model.User
import com.atu.jira.users.UserManager
import com.atu.jira.utils.ResourceState
import com.atu.jira.viewmodel.TicketViewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState

/*@Composable
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
}*/

@Composable
fun CreateTicketScreen(
    project: Project,
    viewModel: TicketViewModel = viewModel { TicketViewModel() },
    onCreate: (Ticket) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {

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

    Column(modifier = Modifier.fillMaxSize()) {

        CommonTopBar(
            title = "New Ticket",
            showBack = true,
            onBack = onBack,
            onLogout = onLogout,
            onSearch = onSearchClick
        )

        if (isTabletOrDesktop) {
            DesktopCreateTicketLayout(project, viewModel, onCreate)
        } else {
            MobileCreateTicketLayout(project, viewModel, onCreate)
        }
    }
}


@Composable
fun MobileCreateTicketLayout(
    project: Project,
    viewModel: TicketViewModel,
    onCreate: (Ticket) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        TicketMainForm(project, viewModel, onCreate)
    }
}

@Composable
fun DesktopCreateTicketLayout(
    project: Project,
    viewModel: TicketViewModel,
    onCreate: (Ticket) -> Unit
) {
    val descriptionState = rememberRichTextState()
    var title by remember { mutableStateOf("") }


    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 LEFT (Main Content)
        Column(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight()
                .padding(end = 12.dp)
        ) {
            TicketMainContent(
                project,
                viewModel,
                descriptionState,
                title,
                onValueChange = { title = it })
        }

        // 🔹 RIGHT (Side Panel)
        Column(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight()
        ) {
            TicketSidePanel(project, viewModel, onCreate, descriptionState, title)
        }
    }
}


@Composable
fun TicketTypeChips(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = TicketType.entries.toTypedArray()

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        items(types) { type ->
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

@Composable
fun TicketMainContent(
    project: Project,
    viewModel: TicketViewModel,
    descriptionState1: RichTextState,
    title1: String,
    onValueChange: (String) -> Unit
) {


    JiraCard(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize()
                .absolutePadding(left = 11.dp, right = 11.dp, top = 11.dp, bottom = 11.dp)
        ) {

            JiraTextField(
                value = title1,
                onValueChange = onValueChange,
                label = "Title",

                )

            Spacer(Modifier.height(16.dp))

            Text("Description", style = MaterialTheme.typography.labelLarge)

            Spacer(Modifier.height(16.dp))

            JiraRichTextEditor(
                state = descriptionState1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TicketSidePanel(
    project: Project,
    viewModel: TicketViewModel,
    onCreate: (Ticket) -> Unit,
    descriptionState1: RichTextState,
    title1: String
) {
    val usersState by viewModel.usersState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var status by remember { mutableStateOf(Status.TODO.value) }
    var selectedTicketType by remember { mutableStateOf(TicketType.BUG.name) }
    var priority by remember { mutableStateOf("medium") }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    JiraCard(Modifier.fillMaxWidth()) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .absolutePadding(11.dp, right = 11.dp, top = 11.dp, bottom = 11.dp)
        ) {

            item {

                Text("Details", style = MaterialTheme.typography.labelLarge)

                TicketTypeChips(selectedType = selectedTicketType, onTypeSelected = {
                    selectedTicketType = it
                })

                SimpleDropdown(
                    label = "Status",
                    options = Status.entries.map { it.value },
                    selectedOption = status,
                    onOptionSelected = { status = it }
                )

                SimpleDropdown(
                    label = "Priority",
                    options = listOf("low", "medium", "high", "critical"),
                    selectedOption = priority,
                    onOptionSelected = { priority = it }
                )


                /* ResourceHandler(state = usersState) { users ->
                     UserSearchView(   // 👈 use your improved search view here
                         users = users,
                         selectedUser = selectedUser,
                         onUserSelected = { selectedUser = it }
                     )
                 }*/

                UserSearchView(   // 👈 use your improved search view here
                    users = UserManager.getAllUsers(),
                    selectedUser = selectedUser,
                    onUserSelected = { selectedUser = it }
                )
                DatePickerField("Start Time", startTime) { startTime = it }
                DatePickerField("End Time", endTime) { endTime = it }
                DatePickerField("Due Date", dueDate) { dueDate = it }

                Spacer(modifier = Modifier.height(10.dp))


                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

                    MainButton(
                        onClick = {
                            viewModel.createTicketWithRPCFunction(
                                Ticket(
                                    title = title1,
                                    description = descriptionState1.toHtml(),
                                    status = status,
                                    priority = priority,
                                    projectId = project.id,
                                    assignedTo = selectedUser?.id,
                                    createdBy = AuthManager.userId,
                                    startTime = startTime,
                                    endTime = endTime,
                                    dueDate = dueDate,
                                    ticketType = selectedTicketType
                                ),
                                onComplete = onCreate
                            )
                        },
                        enabled = actionState !is ResourceState.Loading,
                        text = if (actionState is ResourceState.Loading) "Creating..." else "Create Ticket"
                    )


                }

            }
        }
    }
}

@Composable
fun TicketMainForm(
    project: Project,
    viewModel: TicketViewModel,
    onCreate: (Ticket) -> Unit
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

    // 🔹 Title
    JiraTextField(
        value = title,
        onValueChange = { title = it },
        label = "Title",
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(16.dp))

    // 🔹 Description
    Text(
        "Description",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(Modifier.height(16.dp))

    JiraRichTextEditor(
        state = descriptionState,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(16.dp))

    // 🔹 Status
    SimpleDropdown(
        label = "Status",
        options = Status.entries.map { it.value },
        selectedOption = status,
        onOptionSelected = { status = it }
    )

    Spacer(Modifier.height(16.dp))

    // 🔹 Priority
    SimpleDropdown(
        label = "Priority",
        options = listOf("low", "medium", "high", "critical"),
        selectedOption = priority,
        onOptionSelected = { priority = it }
    )

    Spacer(Modifier.height(16.dp))

    // 🔹 Assignee
    ResourceHandler(state = usersState) { users ->
        UserSearchView(
            users = users,
            selectedUser = selectedUser,
            onUserSelected = { selectedUser = it }
        )
    }

    Spacer(Modifier.height(16.dp))

    // 🔹 Dates
    DatePickerField("Start Time", startTime) { startTime = it }
    Spacer(Modifier.height(16.dp))

    DatePickerField("End Time", endTime) { endTime = it }
    Spacer(Modifier.height(16.dp))

    DatePickerField("Due Date", dueDate) { dueDate = it }

    Spacer(Modifier.height(32.dp))

    // 🔹 Create Button
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
}

