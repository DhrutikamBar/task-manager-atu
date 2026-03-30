package com.atu.jira.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.atu.jira.components.CommonTopBar
import com.atu.jira.model.Project
import com.atu.jira.model.Ticket

@Composable
fun HomeScreen(
    onProjectsClick: (Project) -> Unit,
    onTasksClick: () -> Unit,
    onTaskClick: (Ticket) -> Unit, 
    onAddProject: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        CommonTopBar(title = "Home", onLogout = onLogout, onSearch = onSearchClick)
        
        SecondaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Projects", modifier = Modifier.padding(16.dp))
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            ) {
                Text("Tasks", modifier = Modifier.padding(16.dp))
            }
        }

        when (selectedTab) {
            0 -> ProjectListScreen(
                onProjectClick = onProjectsClick,
                onAddProject = onAddProject, 
                onLogout = onLogout,
                showTopBar = false 
            )
            1 -> TaskListScreen(onTicketClick = onTaskClick)
        }
    }
}
