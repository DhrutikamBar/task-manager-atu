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
import com.atu.jira.model.TicketResponse
import com.atu.jira.model.toCreateRequest
import com.atu.jira.notification.NotificationHelper
import com.atu.jira.repo.createTicket
import com.atu.jira.repo.getTickets
import com.atu.jira.repo.getAllTickets
import com.atu.jira.repo.getUsers
import com.atu.jira.repo.updateTicketStatus
import com.atu.jira.repo.updateTicketWithHistory
import com.atu.jira.repo.getComments
import com.atu.jira.repo.addComment
import com.atu.jira.repo.createTicketWithRPC
import com.atu.jira.repo.getTicketByTicketCode
import com.atu.jira.repo.getTicketHistory
import com.atu.jira.repo.getTicketsByUserId
import com.atu.jira.users.UserManager
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

    private val _historyState =
        MutableStateFlow<ResourceState<List<TicketHistory>>>(ResourceState.Idle)
    val historyState: StateFlow<ResourceState<List<TicketHistory>>> = _historyState.asStateFlow()

    private val _createTicketState = MutableStateFlow<ResourceState<Ticket>>(ResourceState.Idle)
    val createTicketState: StateFlow<ResourceState<Ticket>> = _createTicketState.asStateFlow()

    val ticketByTicketCodeState = MutableStateFlow<ResourceState<Ticket?>>(ResourceState.Loading)


    private val _allTicketsByUserIdState =
        MutableStateFlow<ResourceState<List<Ticket>>>(ResourceState.Idle)
    val allTicketsByUserIdState: StateFlow<ResourceState<List<Ticket>>> =
        _allTicketsByUserIdState.asStateFlow()


    fun fetchTicketByTicketCode(ticketCode: String) {
        viewModelScope.launch {
            ticketByTicketCodeState.value = ResourceState.Loading
            try {
                ticketByTicketCodeState.value =
                    ResourceState.Success(getTicketByTicketCode(ticketCode))
            } catch (e: Exception) {
                ticketByTicketCodeState.value = ResourceState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadTickets(projectId: String) {
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

    fun getAllTicketsByUserId(userId: String) {
        viewModelScope.launch {
            _allTicketsState.value = ResourceState.Loading
            try {
                _allTicketsState.value = ResourceState.Success(getTicketsByUserId(userId))
            } catch (e: Exception) {
                _allTicketsState.value = ResourceState.Error(e.message ?: "Failed to load tasks")
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = ResourceState.Loading
            try {
                val users = getUsers()
                UserManager.setUsers(users)
                _usersState.value = ResourceState.Success(users)
            } catch (e: Exception) {
                _usersState.value = ResourceState.Error(e.message ?: "Failed to load users")
            }
        }
    }

    fun createTicketWithRPCFunction(ticket: Ticket, onComplete: (Ticket) -> Unit) {
        viewModelScope.launch {
            _createTicketState.value = ResourceState.Loading
            try {


                _createTicketState.value = ResourceState.Success(
                    createTicketWithRPC(
                        ticket.toCreateRequest()
                    )
                )

                // extract ticket from the response
                val successResponse = _createTicketState.value as ResourceState.Success<Ticket>
                val newTicket = successResponse.data

                // Notify assigned user using the response
                NotificationHelper.notifyAssignedUser(newTicket.assignedTo!!, newTicket)

                // send new ticket in onComplete callback
                onComplete(newTicket)
                // refresh list
                loadTickets(ticket.projectId!!)


            } catch (e: Exception) {
                e.printStackTrace()
                _createTicketState.value = ResourceState.Error(e.message ?: "Failed to load users")

            }
        }
    }

    fun addTicket(
        title: String,
        description: String,
        status: String,
        priority: String,
        project: Project,
        selectedUser: User?,
        startTime: String?,
        endTime: String?,
        dueDate: String?,
        onComplete: (Ticket) -> Unit
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            _actionState.value = ResourceState.Loading
            try {
                val newTicket = Ticket(
                    title = title,
                    description = description,
                    status = status,
                    projectId = project.id,
                    assignedTo = selectedUser?.id,
                    createdBy = AuthManager.userId,
                    priority = priority,
                    startTime = startTime,
                    endTime = endTime,
                    dueDate = dueDate
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
        priority: String,
        selectedUser: User?,
        startTime: String?,
        endTime: String?,
        dueDate: String?,
        projectId: String,
        onTicketUpdated: (Ticket) -> Unit
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            _updateTicketState.value = ResourceState.Loading
            try {
                val newTicket = Ticket(
                    id = oldTicket.id,
                    title = title,
                    description = description,
                    status = newStatus,
                    projectId = projectId,
                    assignedTo = selectedUser?.name,
                    createdBy = oldTicket.createdBy ?: AuthManager.userId,
                    priority = priority,
                    startTime = startTime,
                    endTime = endTime,
                    dueDate = dueDate,
                    createdAt = oldTicket.createdAt
                )
                updateTicketWithHistory(oldTicket = oldTicket, newTicket = newTicket)
                _updateTicketState.value = ResourceState.Success(Unit)
                onTicketUpdated(newTicket)
            } catch (e: Exception) {
                _updateTicketState.value =
                    ResourceState.Error(e.message ?: "Failed to update ticket")
            }
        }
    }

    fun moveTicket(ticket: Ticket, newStatus: String, projectId: String) {
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

    fun addCommentToTicket(ticketId: String, content: String, parentId: String) {
        viewModelScope.launch {
            try {
                val newComment = Comment(
                    ticketId = ticketId,
                    content = content,
                    createdBy = AuthManager.userId ?: "user_1",
                    parentId = parentId
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