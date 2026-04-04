package com.atu.jira.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.atu.jira.model.TicketHistory
import com.atu.jira.screens.htmlToPlainText
import com.atu.jira.users.UserManager
import com.atu.jira.utils.DateFormatter
import com.atu.jira.utils.ResourceState
import com.atu.jira.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    historyState: ResourceState<List<TicketHistory>>,
    drawerState: DrawerState,

    ) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(360.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 🔹 HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Activity",
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider()

            // 🔹 CONTENT
            ResourceHandler(
                state = historyState,
                onLoading = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(5) {
                            HistoryShimmerItem()
                        }
                    }
                }
            ) { historyItems ->
                if (historyItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No activity yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(historyItems) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {


                                Spacer(Modifier.width(12.dp))

                                // 🔹 CONTENT
                                Column(
                                    modifier = Modifier
                                        .padding(bottom = 16.dp)
                                        .weight(1f)
                                ) {

                                    HistoryTimelineItem(item)

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        "By ${UserManager.getUserName(item.changedBy)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )

                                    Text(
                                        "On ${Utils.formatDateTime(item.changedAt ?: "")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun getFieldType(field: String): String {
    return when {
        field.contains("description", true) -> "description"
        field.contains("assign", true) -> "assignee"
        field.contains("status", true) -> "status"
        else -> "default"
    }
}


@Composable
fun HistoryTimelineItem(item: TicketHistory) {

    val field = item.fieldName.replace("_", " ")
    val fieldType = field.lowercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        // 🔥 Timeline (Dot + Vertical Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {

            // Dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
            )

            // Vertical Line (FIXED)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .heightIn(min = 40.dp, max = 80.dp) // 🔥 adjust based on content
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
            )
        }

        Spacer(Modifier.width(12.dp))

        // 🔥 Content
        Column(modifier = Modifier.weight(1f)) {

            // ✅ NORMAL SENTENCE STYLE
            Text(
                buildAnnotatedString {

                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(field)
                    }

                    append(" changed from ")

                    // Old Value
                    withStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        append(
                            when {
                                fieldType.contains("description") ->
                                    htmlToPlainText(item.oldValue ?: "")

                                fieldType.contains("date") ->
                                    DateFormatter.format(item.oldValue ?: "")

                                fieldType.contains("assign") ->
                                    UserManager.getUserName(item.oldValue)

                                else -> item.oldValue ?: ""
                            }
                        )
                    }

                    append(" to ")

                    // New Value
                    withStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(
                            when {
                                fieldType.contains("description") ->
                                    htmlToPlainText(item.newValue ?: "")

                                fieldType.contains("date") ->
                                    DateFormatter.format(item.newValue ?: "")

                                fieldType.contains("assign") ->
                                    UserManager.getUserName(item.newValue)

                                else -> item.newValue ?: ""
                            }
                        )
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )


        }
    }
}


@Composable
fun HistoryItemView(item: TicketHistory) {

    val fieldType = getFieldType(item.fieldName)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                RoundedCornerShape(10.dp)
            )
    ) {

        // 🔹 Header (Field Name)
        Text(
            text = item.fieldName.replace("_", " ").uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(6.dp))

        when (fieldType) {

            // 📝 Description
            "description" -> {
                Text(
                    text = "Old: ${htmlToPlainText(item.oldValue ?: "")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "New: ${htmlToPlainText(item.newValue ?: "")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 👤 Assignee
            "assignee" -> {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = UserManager.getUserName(item.oldValue),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = UserManager.getUserName(item.newValue),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 🔄 Status
            "status" -> {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    StatusChip(item.oldValue ?: "")

                    Spacer(Modifier.width(8.dp))

                    Icon(Icons.Default.ArrowForward, null)

                    Spacer(Modifier.width(8.dp))

                    StatusChip(item.newValue ?: "")
                }
            }

            // 🔹 Default
            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = item.oldValue ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(Icons.Default.ArrowForward, null)

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = item.newValue ?: "",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }


}

@Composable
fun StatusChip(status: String) {

    val color = when (status.uppercase()) {
        "TODO" -> Color.Gray
        "IN_PROGRESS" -> Color(0xFF2196F3)
        "DONE" -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.replace("_", " "),
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}