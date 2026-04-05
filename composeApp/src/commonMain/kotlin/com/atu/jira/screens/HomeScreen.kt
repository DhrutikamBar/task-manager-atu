package com.atu.jira.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.atu.jira.components.CommonTopBar
import com.atu.jira.model.Project
import com.atu.jira.model.Ticket
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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

        /*SecondaryTabRow(selectedTabIndex = selectedTab) {
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
        }*/

        /*  AdvancedHomeTabs(
              selectedTab = selectedTab,
              onTabSelected = { selectedTab = it }
          )*/

        CompactHomeTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenV2(
    onProjectsClick: (Project) -> Unit,
    onTasksClick: () -> Unit,
    onTaskClick: (Ticket) -> Unit,
    onAddProject: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {

        CommonTopBar(
            title = "Home",
            onLogout = onLogout,
            onSearch = onSearchClick
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            // 🔹 Compact Swipeable Tabs
            CompactHomeTabsSwipeable(
                selectedTab = pagerState.currentPage,
                onTabSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = index,
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            )
        }


        // 🔹 Swipeable Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->

            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val scale = lerp(0.95f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
            val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            ) {
                when (page) {
                    0 -> ProjectListScreen(
                        onProjectClick = onProjectsClick,
                        onAddProject = onAddProject,
                        onLogout = onLogout,
                        showTopBar = false
                    )

                    1 -> TaskListScreen(
                        onTicketClick = onTaskClick
                    )
                }
            }


        }
    }
}


@Composable
fun CompactHomeTabsSwipeable(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Projects", "Tasks")

    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabs.forEachIndexed { index, title ->

            val isSelected = selectedTab == index

            val bgColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )

            val textColor by animateColorAsState(
                if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(bgColor)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = if (title == "Projects")
                        Icons.Default.Folder else Icons.Default.List,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = title,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun AdvancedHomeTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Projects", "Tasks")

    Row(
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->

            val isSelected = selectedTab == index

            val backgroundColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )

            val textColor by animateColorAsState(
                if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(backgroundColor)
                    .clickable { onTabSelected(index) }
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Optional icons (nice touch)
                    if (title == "Projects") {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = title,
                        color = textColor,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun CompactHomeTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Projects", "Tasks")

    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabs.forEachIndexed { index, title ->

            val isSelected = selectedTab == index

            val bgColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )

            val textColor by animateColorAsState(
                if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(bgColor)
                    .clickable { onTabSelected(index) }
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = if (title == "Projects") Icons.Default.Folder else Icons.Default.List,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = title,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
