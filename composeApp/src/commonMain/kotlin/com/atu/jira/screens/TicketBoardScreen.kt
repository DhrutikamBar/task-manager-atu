package com.atu.jira.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.model.Project
import com.atu.jira.model.Status
import com.atu.jira.model.Ticket
import com.atu.jira.viewmodel.TicketViewModel
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun TicketBoardScreenMobileV2(
    tickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    project: Project
) {

    val viewModel: TicketViewModel = remember {
        getKoin().get<TicketViewModel>()
    }
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        StatusChips(
            selectedStatus = selectedStatus,
            onStatusSelected = { selectedStatus = it }
        )

        // 🔹 Ticket List (ONLY ONE SCROLL)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            val filteredTickets =
                if (selectedStatus == null || selectedStatus?.lowercase() == "all") {
                    tickets
                } else {
                    tickets.filter { it.status == selectedStatus }
                }

            items(filteredTickets) { ticket ->

                TicketCardV2(
                    ticket = ticket,
                    onTicketClick = { onTicketClick(ticket) },
                    onMove = {
                        when (selectedStatus) {
                            Status.TODO.value ->
                                viewModel.moveTicket(it, Status.IN_PROGRESS.value, project.id)

                            Status.IN_PROGRESS.value ->
                                viewModel.moveTicket(it, Status.CLOSED.value, project.id)

                            else -> null
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun TicketBoardScreenMobile(
    tickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    project: Project
) {
    val viewModel: TicketViewModel = remember {
        getKoin().get<TicketViewModel>()
    }
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
            onMove = { viewModel.moveTicket(it, Status.CLOSED.value, project.id) },
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
        )

        StatusColumn(
            title = "Done",
            tickets = tickets.filter { it.status == Status.CLOSED.value },
            onTicketClick = onTicketClick,
            onMove = null,
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
        )
    }
}


@Composable
fun TicketBoardScreenDesktop(
    tickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit,
    project: Project
) {
    val viewModel: TicketViewModel = remember {
        getKoin().get<TicketViewModel>()
    }
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
            onMove = { viewModel.moveTicket(it, Status.CLOSED.value, project.id) },
            modifier = Modifier.weight(1f)
        )

        StatusColumn(
            title = "Done",
            tickets = tickets.filter { it.status == Status.CLOSED.value },
            onTicketClick = onTicketClick,
            onMove = null,
            modifier = Modifier.weight(1f)
        )
    }
}