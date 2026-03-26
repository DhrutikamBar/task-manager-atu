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

class ProjectViewModel : ViewModel() {
    private val _projectsState = MutableStateFlow<ResourceState<List<Project>>>(ResourceState.Idle)
    val projectsState: StateFlow<ResourceState<List<Project>>> = _projectsState.asStateFlow()

    private val _createProjectState = MutableStateFlow<ResourceState<Project>>(ResourceState.Idle)
    val createProjectState: StateFlow<ResourceState<Project>> = _createProjectState.asStateFlow()

    // Backward compatibility for simple loaders
    val isLoading: StateFlow<Boolean> = MutableStateFlow(false) 
    val error: StateFlow<String?> = MutableStateFlow(null)

    fun loadProjects() {
        viewModelScope.launch {
            _projectsState.value = ResourceState.Loading
            try {
                _projectsState.value = ResourceState.Success(getProjects())
            } catch (e: Exception) {
                _projectsState.value = ResourceState.Error(e.message ?: "Failed to load projects")
            }
        }
    }

    fun addProject(name: String, onComplete: (Project) -> Unit) {
        if (name.isBlank()) return
        
        viewModelScope.launch {
            _createProjectState.value = ResourceState.Loading
            try {
                val project = Project(
                    id = (0..Long.MAX_VALUE).random(),
                    name = name
                )
                createProject(project)
                _createProjectState.value = ResourceState.Success(project)
                onComplete(project)
            } catch (e: Exception) {
                _createProjectState.value = ResourceState.Error(e.message ?: "Failed to create project")
            }
        }
    }
    
    fun resetCreateState() {
        _createProjectState.value = ResourceState.Idle
    }
}
