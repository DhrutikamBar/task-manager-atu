package com.atu.jira.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.atu.jira.LocalTicketEditMode
import com.atu.jira.model.User
import com.atu.jira.screens.DatePickerField
import com.atu.jira.screens.SimpleDropdown
import com.atu.jira.screens.UserDropdown

@Composable
fun BaseEditableField(
    modifier: Modifier = Modifier,
    viewMode: @Composable () -> Unit,
    editMode: @Composable () -> Unit
) {
    val isEditMode = LocalTicketEditMode.current

    Box(modifier = modifier) {
        if (isEditMode) {
            editMode()
        } else {
            viewMode()
        }
    }
}

@Composable
fun InlineEditableText(
    field: String,
    value: String,
    editState: InlineEditState,
    onSave: (String) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    var text by remember { mutableStateOf(value) }

    val isEditing = editState.isEditing(field)

    if (isEditing) {
        JiraTextField(
            value = text,
            onValueChange = { text = it },
            label = "",
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (!it.isFocused) {
                        onSave(text)
                        editState.stopEditing()
                    }
                }
        )
    } else {
        Text(
            text = value,
            style = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    editState.startEditing(field)
                }
                .padding(4.dp)
        )
    }
}

@Composable
fun InlineEditableDropdown(
    field: String,
    value: String,
    options: List<String>,
    editState: InlineEditState,
    onSave: (String) -> Unit
) {
    val isEditing = editState.isEditing(field)

    if (isEditing) {
        SimpleDropdown(
            label = "",
            options = options,
            selectedOption = value,
            onOptionSelected = {
                onSave(it)
                editState.stopEditing()
            }
        )
    } else {
        Row(
            modifier = Modifier
                .clickable { editState.startEditing(field) }
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
        }
    }
}


@Composable
fun InlineEditableUser(
    field: String,
    users: List<User>,
    selectedUser: User?,
    editState: InlineEditState,
    onSave: (User) -> Unit
) {
    val isEditing = editState.isEditing(field)

    if (isEditing) {
        UserDropdown(
            users = users,
            selectedUser = selectedUser,
            onUserSelected = {
                onSave(it)
                editState.stopEditing()
            }
        )
    } else {
        Row(
            modifier = Modifier
                .clickable { editState.startEditing(field) }
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedUser?.name ?: "Unassigned")
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
        }
    }
}


@Composable
fun InlineEditableDate(
    field: String,
    value: String,
    editState: InlineEditState,
    onSave: (String) -> Unit
) {
    val isEditing = editState.isEditing(field)

    if (isEditing) {
        DatePickerField(
            label = "",
            selectedDate = value,
            onDateSelected = {
                onSave(it)
                editState.stopEditing()
            }
        )
    } else {
        Row(
            modifier = Modifier
                .clickable { editState.startEditing(field) }
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (value.isBlank()) "Set Date" else value)
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
        }
    }
}