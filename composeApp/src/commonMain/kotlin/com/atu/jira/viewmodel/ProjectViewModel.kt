package com.atu.jira.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atu.jira.model.Project
import com.atu.jira.repo.createProject
import com.atu.jira.repo.getProjects
import com.atu.jira.utils.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.atu.jira.auth.AuthManager
import com.atu.jira.model.User
import com.atu.jira.repo.getUsers
import com.atu.jira.users.UserManager
import kotlin.random.Random

class ProjectViewModel : ViewModel() {
    private val _projectsState = MutableStateFlow<ResourceState<List<Project>>>(ResourceState.Idle)
    val projectsState: StateFlow<ResourceState<List<Project>>> = _projectsState.asStateFlow()

    private val _createProjectState = MutableStateFlow<ResourceState<Project>>(ResourceState.Idle)
    val createProjectState: StateFlow<ResourceState<Project>> = _createProjectState.asStateFlow()

    private val _usersState = MutableStateFlow<ResourceState<List<User>>>(ResourceState.Idle)
    val usersState: StateFlow<ResourceState<List<User>>> = _usersState.asStateFlow()

    private var isLoaded = false

    fun loadProjects(forceRefresh: Boolean = false) {

        if (isLoaded && !forceRefresh) return
        isLoaded = true
        viewModelScope.launch {
            _projectsState.value = ResourceState.Loading
            try {
                _projectsState.value = ResourceState.Success(getProjects())
            } catch (e: Exception) {
                _projectsState.value = ResourceState.Error(e.message ?: "Failed to load projects")
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

    fun addProject(
        name: String,
        projectCode: String,
        description: String?,
        onComplete: (Project) -> Unit
    ) {
        if (name.isBlank() || projectCode.isBlank()) return

        viewModelScope.launch {
            _createProjectState.value = ResourceState.Loading
            try {
                val project = Project(
                    name = name,
                    projectCode = projectCode.uppercase(),
                    description = description,
                    createdBy = AuthManager.userId,
                    ticketSequence = 0,
                    isActive = true
                )
                createProject(project)
                _createProjectState.value = ResourceState.Success(project)
                onComplete(project)
            } catch (e: Exception) {
                _createProjectState.value =
                    ResourceState.Error(e.message ?: "Failed to create project")
            }
        }
    }

    fun resetCreateState() {
        _createProjectState.value = ResourceState.Idle
    }
}
