package com.atu.jira.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MainButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    val primary = MaterialTheme.colorScheme.primary
    val disabled = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    val buttonColor = if (enabled) primary else disabled
    val shadowColor = if (enabled) primary.copy(alpha = 0.6f) else Color.LightGray.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {

        // ---------- Bottom shadow ----------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(shadowColor)
        )

        // ---------- Top button ----------
        Button(
            onClick = {
                if (!enabled) return@Button

                scope.launch {
                    // press down
                    offsetY.animateTo(
                        4f,
                        animationSpec = tween(80)
                    )

                    // bounce up
                    offsetY.animateTo(
                        0f,
                        animationSpec = spring(
                            dampingRatio = 0.45f,
                            stiffness = Spring.StiffnessMedium
                        )
                    )

                    onClick()
                }
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(0, offsetY.value.roundToInt())
                }
                .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = buttonColor
            )
        ) {

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun JiraButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val offsetY = remember { Animatable(0f) }

    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = when {
        !enabled -> Color(0xFFE0E0E0) // 🔹 proper grey
        isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
        else -> MaterialTheme.colorScheme.primary
    }

    val contentColor = when {
        !enabled -> Color(0xFF9E9E9E) // 🔹 muted text
        else -> Color.White
    }


    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(6.dp), // 🔹 Jira uses smaller radius
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp) // 🔹 smaller than your current 58dp
            .offset {
                IntOffset(0, offsetY.value.roundToInt())
            }
            .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}
