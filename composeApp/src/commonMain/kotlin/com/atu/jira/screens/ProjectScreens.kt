package com.atu.jira.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.components.CommonTopBar
import com.atu.jira.components.ResourceHandler
import com.atu.jira.model.Project
import com.atu.jira.repo.safeCall
import com.atu.jira.viewmodel.ProjectViewModel
import com.atu.jira.utils.ResourceState

@Composable
fun ProjectListScreen(
    viewModel: ProjectViewModel = viewModel { ProjectViewModel() },
    onProjectClick: (Project) -> Unit, 
    onAddProject: () -> Unit,
    onLogout: () -> Unit = {},
    showTopBar: Boolean = true // Added this parameter
) {
    val projectsState by viewModel.projectsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (showTopBar) {
            CommonTopBar(title = "Projects", onLogout = onLogout)
        }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Projects", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = onAddProject) {
                    Text("+ New")
                }
            }

            Spacer(Modifier.height(16.dp))

            ResourceHandler(state = projectsState) { projects ->
                if (projects.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No projects found. Create one to get started!", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(projects) { project ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onProjectClick(project) },
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Text(
                                    text = project.name,
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
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
    viewModel: ProjectViewModel = viewModel { ProjectViewModel() },
    onCreate: (Project) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    val createProjectState by viewModel.createProjectState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        CommonTopBar(
            title = "New Project",
            showBack = true,
            onBack = onBack,
            onLogout = onLogout
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = createProjectState !is ResourceState.Loading
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.addProject(projectName, onCreate) },
            enabled = projectName.isNotBlank() && createProjectState !is ResourceState.Loading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (createProjectState is ResourceState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Create Project")
            }
        }

        if (createProjectState is ResourceState.Error) {
            Text(
                text = (createProjectState as ResourceState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
