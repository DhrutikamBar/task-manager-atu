package com.atu.jira.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atu.jira.model.Project
import com.atu.jira.model.Status
import com.atu.jira.model.Ticket
import com.atu.jira.model.User
import com.atu.jira.auth.AuthManager
import com.atu.jira.repo.createTicket
import com.atu.jira.repo.getTickets
import com.atu.jira.repo.getUsers
import com.atu.jira.repo.updateTicketStatus
import com.atu.jira.utils.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {
    private val _ticketsState = MutableStateFlow<ResourceState<List<Ticket>>>(ResourceState.Idle)
    val ticketsState: StateFlow<ResourceState<List<Ticket>>> = _ticketsState.asStateFlow()

    private val _usersState = MutableStateFlow<ResourceState<List<User>>>(ResourceState.Idle)
    val usersState: StateFlow<ResourceState<List<User>>> = _usersState.asStateFlow()
    
    private val _actionState = MutableStateFlow<ResourceState<Unit>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<Unit>> = _actionState.asStateFlow()

    fun loadTickets(projectId: Long) {
        viewModelScope.launch {
            _ticketsState.value = ResourceState.Loading
            try {
                _ticketsState.value = ResourceState.Success(getTickets(projectId))
            } catch (e: Exception) {
                _ticketsState.value = ResourceState.Error(e.message ?: "Failed to load tickets")
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = ResourceState.Loading
            try {
                _usersState.value = ResourceState.Success(getUsers())
            } catch (e: Exception) {
                _usersState.value = ResourceState.Error(e.message ?: "Failed to load users")
            }
        }
    }

    fun addTicket(
        title: String,
        description: String,
        project: Project,
        selectedUser: User?,
        onComplete: (Ticket) -> Unit
    ) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            try {
                val newTicket = Ticket(
                    title = title,
                    description = description,
                    status = Status.TODO.value,
                    projectId = project.id,
                    assignedTo = selectedUser?.name,
                    createdBy = AuthManager.userId,
                    priority = "medium"
                )
                createTicket(newTicket)
                _actionState.value = ResourceState.Success(Unit)
                onComplete(newTicket)
            } catch (e: Exception) {
                _actionState.value = ResourceState.Error(e.message ?: "Failed to create ticket")
            }
        }
    }

    fun moveTicket(ticket: Ticket, newStatus: String, projectId: Long) {
        viewModelScope.launch {
            try {
                updateTicketStatus(ticket.id!!, newStatus)
                loadTickets(projectId) 
            } catch (e: Exception) {
                // Handle background move error
            }
        }
    }
}
