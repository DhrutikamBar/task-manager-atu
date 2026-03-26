package com.atu.jira.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atu.jira.auth.AuthManager
import com.atu.jira.utils.ResourceState
import jiraatu.composeapp.generated.resources.Res
import jiraatu.composeapp.generated.resources.ic_atu
import org.jetbrains.compose.resources.painterResource

@Composable
fun <T> ResourceHandler(
    state: ResourceState<T>,
    onIdle: @Composable () -> Unit = {},
    onLoading: @Composable () -> Unit = { LoadingUI() },
    onError: @Composable (String) -> Unit = { ErrorUI(it) },
    onSuccess: @Composable (T) -> Unit
) {
    when (state) {
        is ResourceState.Idle -> onIdle()
        is ResourceState.Loading -> onLoading()
        is ResourceState.Error -> onError(state.message)
        is ResourceState.Success -> onSuccess(state.data)
    }
}

@Composable
fun LoadingUI() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    }
}

@Composable
fun ErrorUI(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun CommonTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onLogout: () -> Unit = {}
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack && onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
        }

        Image(
            painter = painterResource(Res.drawable.ic_atu),
            contentDescription = "Logo",
            modifier = Modifier.size(50.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Box {
            IconButton(onClick = { showProfileMenu = true }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            DropdownMenu(
                expanded = showProfileMenu,
                onDismissRequest = { showProfileMenu = false }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "User Details",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Email: ${AuthManager.email ?: "N/A"}")
                    Text("Role: ${AuthManager.role ?: "User"}")
                    AuthManager.userName?.let {
                        Text("Name: $it")
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showProfileMenu = false
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Logout", color = Color.White)
                    }
                }
            }
        }
    }
}