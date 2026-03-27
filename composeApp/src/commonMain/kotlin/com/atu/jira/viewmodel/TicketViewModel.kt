package com.atu.jira.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atu.jira.model.Project
import com.atu.jira.model.Status
import com.atu.jira.model.Ticket
import com.atu.jira.model.User
import com.atu.jira.model.Comment
import com.atu.jira.model.TicketHistory
import com.atu.jira.auth.AuthManager
import com.atu.jira.repo.createTicket
import com.atu.jira.repo.getTickets
import com.atu.jira.repo.getAllTickets
import com.atu.jira.repo.getUsers
import com.atu.jira.repo.updateTicketStatus
import com.atu.jira.repo.updateTicketWithHistory
import com.atu.jira.repo.getComments
import com.atu.jira.repo.addComment
import com.atu.jira.repo.getTicketHistory
import com.atu.jira.utils.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel : ViewModel() {
    private val _ticketsState = MutableStateFlow<ResourceState<List<Ticket>>>(ResourceState.Idle)
    val ticketsState: StateFlow<ResourceState<List<Ticket>>> = _ticketsState.asStateFlow()

    private val _allTicketsState = MutableStateFlow<ResourceState<List<Ticket>>>(ResourceState.Idle)
    val allTicketsState: StateFlow<ResourceState<List<Ticket>>> = _allTicketsState.asStateFlow()

    private val _usersState = MutableStateFlow<ResourceState<List<User>>>(ResourceState.Idle)
    val usersState: StateFlow<ResourceState<List<User>>> = _usersState.asStateFlow()
    
    private val _actionState = MutableStateFlow<ResourceState<Unit>>(ResourceState.Idle)
    val actionState: StateFlow<ResourceState<Unit>> = _actionState.asStateFlow()

    private val _updateTicketState = MutableStateFlow<ResourceState<Unit>>(ResourceState.Idle)
    val updateTicketState: StateFlow<ResourceState<Unit>> = _updateTicketState.asStateFlow()

    private val _commentsState = MutableStateFlow<ResourceState<List<Comment>>>(ResourceState.Idle)
    val commentsState: StateFlow<ResourceState<List<Comment>>> = _commentsState.asStateFlow()

    private val _historyState = MutableStateFlow<ResourceState<List<TicketHistory>>>(ResourceState.Idle)
    val historyState: StateFlow<ResourceState<List<TicketHistory>>> = _historyState.asStateFlow()


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

    fun loadAllTickets() {
        viewModelScope.launch {
            _allTicketsState.value = ResourceState.Loading
            try {
                _allTicketsState.value = ResourceState.Success(getAllTickets())
            } catch (e: Exception) {
                _allTicketsState.value = ResourceState.Error(e.message ?: "Failed to load tasks")
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

    fun updateTicket(
        oldTicket: Ticket,
        title: String,
        description: String,
        newStatus: String,
        projectId : Long,
        onTicketUpdated: (Ticket) -> Unit
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            _updateTicketState.value = ResourceState.Loading
            try {
                val newTicket = Ticket(
                    title = title,
                    description = description,
                    status = Status.TODO.value,
                    projectId = projectId,
                    assignedTo = "Rabil", // hardcoded in original file
                    createdBy = AuthManager.userId,
                    priority = "medium"
                )
                updateTicketWithHistory(ticket = oldTicket, newDescription = description, newStatus = newStatus)
                _updateTicketState.value = ResourceState.Success(Unit)
                onTicketUpdated(newTicket)
            } catch (e: Exception) {
                _updateTicketState.value = ResourceState.Error(e.message ?: "Failed to update ticket")
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

    fun loadComments(ticketId: String) {
        viewModelScope.launch {
            _commentsState.value = ResourceState.Loading
            try {
                _commentsState.value = ResourceState.Success(getComments(ticketId))
            } catch (e: Exception) {
                _commentsState.value = ResourceState.Error(e.message ?: "Failed to load comments")
            }
        }
    }

    fun addCommentToTicket(ticketId: String, content: String) {
        viewModelScope.launch {
            try {
                val newComment = Comment(
                    ticketId = ticketId,
                    content = content,
                    createdBy = AuthManager.userId ?: "user_1" 
                )
                addComment(newComment)
                loadComments(ticketId)
            } catch (e: Exception) {
                // Handle background comment error
            }
        }
    }

    fun loadTicketHistory(ticketId: String) {
        viewModelScope.launch {
            _historyState.value = ResourceState.Loading
            try {
                _historyState.value = ResourceState.Success(getTicketHistory(ticketId))
            } catch (e: Exception) {
                 _historyState.value = ResourceState.Error(e.message ?: "Failed to load history")
            }
        }
    }
}