package com.atu.jira.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.auth.AuthManager
import com.atu.jira.components.CommonTopBar
import com.atu.jira.components.JiraButton
import com.atu.jira.components.JiraTextField
import com.atu.jira.components.MainButton
import com.atu.jira.components.ProjectShimmerItem
import com.atu.jira.components.UIStateHandler
import com.atu.jira.model.Project
import com.atu.jira.users.UserManager
import com.atu.jira.viewmodel.ProjectViewModel
import com.atu.jira.utils.ResourceState
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun ProjectListScreen(
    onProjectClick: (Project) -> Unit,
    onAddProject: () -> Unit,
    onSearchClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    showTopBar: Boolean = true // Added this parameter
) {
    val viewModel: ProjectViewModel = remember {
        getKoin().get<ProjectViewModel>()
    }
    val projectsState by viewModel.projectsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }


    Column(modifier = Modifier.fillMaxHeight().widthIn(max = 500.dp)) {
        if (showTopBar) {
            CommonTopBar(title = "Projects", onLogout = onLogout, onSearch = onSearchClick)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("", style = MaterialTheme.typography.headlineSmall)

                /*Box(modifier = Modifier.width(200.dp), contentAlignment = Alignment.Center) {
                    JiraButton(
                        text = "+ New",
                        enabled = UserManager.isSuperAdmin(AuthManager.userId),
                        onClick = {
                            onAddProject()
                        }
                    )
                }*/
                if (UserManager.isSuperAdmin(AuthManager.userId)) {
                    Text(
                        "+ New Project",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable {
                            onAddProject()
                        })
                }


            }

            Spacer(Modifier.height(16.dp))

            UIStateHandler(state = projectsState, onLoading = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(5) {
                        ProjectShimmerItem()
                    }
                }
            }) { projects ->
                if (projects.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No projects found. Create one to get started!", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(projects) { project ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onProjectClick(project) }
                                    .pointerHoverIcon(PointerIcon.Hand),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = project.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = project.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Code: ${project.projectCode}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Open Project",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
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
fun CreateProjectScreen(
    onCreate: (Project) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    val viewModel: ProjectViewModel = remember {
        getKoin().get<ProjectViewModel>()
    }
    var projectName by remember { mutableStateOf("") }
    var projectCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val createProjectState by viewModel.createProjectState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CommonTopBar(
            title = "New Project",
            showBack = true,
            onBack = onBack,
            onLogout = onLogout,
            onSearch = onSearchClick
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(Modifier.height(24.dp))

            JiraTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = "Project Name",
                modifier = Modifier.fillMaxWidth(),
                enabled = createProjectState !is ResourceState.Loading
            )

            Spacer(Modifier.height(16.dp))

            JiraTextField(
                value = projectCode,
                onValueChange = { if (it.length <= 5) projectCode = it.uppercase() },
                label = "Project Code (e.g. ATU)",
                modifier = Modifier.fillMaxWidth(),
                enabled = createProjectState !is ResourceState.Loading
            )

            Spacer(Modifier.height(16.dp))

            JiraTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description (Optional)",
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = createProjectState !is ResourceState.Loading
            )

            Spacer(Modifier.height(24.dp))

            MainButton(
                text = if (createProjectState is ResourceState.Loading) "Creating..." else "Create Project",
                enabled = projectName.isNotBlank() && projectCode.isNotBlank() && createProjectState !is ResourceState.Loading,
                onClick = {
                    viewModel.addProject(
                        projectName,
                        projectCode,
                        description.takeIf { it.isNotBlank() },
                        onCreate
                    )
                }
            )

            if (createProjectState is ResourceState.Error) {
                Text(
                    text = (createProjectState as ResourceState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
