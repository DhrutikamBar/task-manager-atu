package com.atu.jira.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atu.jira.components.CommonTopBar
import com.atu.jira.model.Project

@Composable
fun HomeScreen(
    onProjectsClick: (Project) -> Unit,
    onTasksClick: () -> Unit,
    onAddProject: () -> Unit, // Added this
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(title = "Home", onLogout = onLogout)
        
        SecondaryTabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Projects", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Tasks", modifier = Modifier.padding(16.dp))
            }
        }

        when (selectedTab) {
            0 -> ProjectListScreen(
                onProjectClick = onProjectsClick,
                onAddProject = onAddProject, // Pass here
                onLogout = onLogout,
                showTopBar = false // Hide nested TopBar
            )
            1 -> TaskListScreen()
        }
    }
}
