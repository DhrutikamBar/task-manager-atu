package com.atu.jira.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class InlineEditState {
    var editingField by mutableStateOf<String?>(null)

    fun startEditing(field: String) {
        editingField = field
    }

    fun stopEditing() {
        editingField = null
    }

    fun isEditing(field: String) = editingField == field
}