package com.atu.jira.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

// --- Shimmer Modifier ---
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.4f),
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation, y = translateAnimation)
    )
    
    this.background(brush)
}

// --- Specific Shimmer Placeholders ---
@Composable
fun ProjectShimmerItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.height(18.dp).fillMaxWidth(0.6f).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.4f).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
        }
    }
}

@Composable
fun TicketShimmerItem() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().shimmerEffect())
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.8f).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.height(12.dp).width(60.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    Box(modifier = Modifier.height(12.dp).width(50.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                }

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).shimmerEffect())
                    Spacer(Modifier.width(6.dp))
                    Box(modifier = Modifier.height(12.dp).width(80.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                }
            }
        }
    }
}

@Composable
fun CommentShimmerItem() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).shimmerEffect())
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Box(modifier = Modifier.height(14.dp).width(100.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.height(60.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)).shimmerEffect())
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun HistoryShimmerItem() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).shimmerEffect())
            Box(modifier = Modifier.width(2.dp).height(60.dp).shimmerEffect())
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f).padding(bottom = 16.dp)) {
            Box(modifier = Modifier.height(12.dp).width(60.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.height(14.dp).fillMaxWidth(0.9f).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.height(10.dp).width(80.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(Modifier.height(4.dp))
            Box(modifier = Modifier.height(10.dp).width(100.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        }
    }
}


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
