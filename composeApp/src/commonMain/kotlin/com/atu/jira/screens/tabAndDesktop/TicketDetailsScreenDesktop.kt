package com.atu.jira.screens.tabAndDesktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.components.BaseEditableField
import com.atu.jira.components.CommentShimmerItem
import com.atu.jira.components.JiraCard
import com.atu.jira.components.JiraTextField
import com.atu.jira.components.UIStateHandler
import com.atu.jira.model.Comment
import com.atu.jira.model.Ticket
import com.atu.jira.screens.CommentItem
import com.atu.jira.screens.JiraCommentBoxV2
import com.atu.jira.screens.JiraRichTextEditor
import com.atu.jira.screens.ReplyItemForComment
import com.atu.jira.screens.TicketDetailsListComponent
import com.atu.jira.users.UserManager
import com.atu.jira.utils.ResourceState
import com.atu.jira.viewmodel.TicketViewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun TicketDetailsUIWeb(
    ticket: Ticket,
    onTicketEditClick: (Ticket) -> Unit,
    scope: CoroutineScope,
    drawerState: DrawerState,
    descState: RichTextState,
    commentsState: ResourceState<List<Comment>>
) {
    val viewModel: TicketViewModel = remember {
        getKoin().get<TicketViewModel>()
    }
    var editableTicket = viewModel.editableTicket

    Row(modifier = Modifier.fillMaxWidth().absolutePadding(left = 11.dp, right = 11.dp)) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .weight(.6f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔹 Ticket Info Card
            repeat(1) {
                JiraCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = ticket.ticketCode ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(6.dp))
                        BaseEditableField(
                            viewMode = {
                                Text(
                                    text = ticket.title,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            },
                            editMode = {
                                JiraTextField(
                                    value = editableTicket?.title ?: "",
                                    onValueChange = {
                                       // editableTicket = editableTicket?.copy(title = it)
                                        viewModel.updateTitle(it)
                                    },
                                    label = "Title"
                                )
                            }
                        )

                        /*  Text(
                              text = ticket.title,
                              style = MaterialTheme.typography.headlineSmall
                          )*/

                        Spacer(Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            BaseEditableField(viewMode = {
                                RichText(
                                    state = descState,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }, editMode = {
                                JiraRichTextEditor(
                                    state = descState,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            })

                        }
                    }
                }
            }

            // 🔹 Add Comment Card
            repeat(1) {
                BaseEditableField(viewMode = {
                    JiraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Add Comment",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(12.dp))

                            val users = UserManager.getAllUsers()

                            JiraCommentBoxV2(
                                ticket,
                                users,
                                onSend = { htmlComment, mentionedIds ->
                                    viewModel.addCommentToTicket(
                                        ticket.id.toString(),
                                        htmlComment,
                                        parentId = "", onCommentAdded = {
                                            viewModel.notifyToMentionedUsers(
                                                validIds = mentionedIds,
                                                ticket = ticket,
                                                html = htmlComment
                                            )
                                        }
                                    )
                                })
                        }
                    }
                }, editMode = {


                })

            }

            // 🔹 Comments Card
            repeat(1) {
                BaseEditableField(viewMode = {

                    JiraCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Comments",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(Modifier.height(12.dp))

                            UIStateHandler(
                                state = commentsState,
                                onLoading = {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        repeat(3) { CommentShimmerItem() }
                                    }
                                }
                            ) { comments ->

                                val parentComments = comments.filter { it.parentId == null }

                                fun getReplies(parentId: String): List<Comment> {
                                    return comments.filter { it.parentId == parentId }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                    parentComments.forEach { parent ->

                                        CommentItem(parent, ticket = ticket)

                                        val replies = getReplies(parent.id ?: "")

                                        if (replies.isNotEmpty()) {
                                            Column(
                                                modifier = Modifier.padding(start = 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                replies.forEach { reply ->
                                                    ReplyItemForComment(reply)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, editMode = {

                })

            }
        }


        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(.4f)) {
            TicketDetailsListComponent(
                ticket,
                onTicketEditClick,
                onclickLoadTicketHistory = {
                    scope.launch {
                        viewModel.loadTicketHistory(ticket.id.toString())
                        drawerState.open()
                    }
                }, onTicketUpdated = {

                    viewModel.fetchTicketByTicketCode(ticket?.ticketCode ?: "")


                }, descriptionState = descState
            )


        }

    }
}