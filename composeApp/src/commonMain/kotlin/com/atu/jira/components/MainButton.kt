package com.atu.jira.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    val green = MaterialTheme.colorScheme.primary
    val fadeGreen = MaterialTheme.colorScheme.onPrimary

    val buttonColor =
        if (enabled) green
        else fadeGreen

    val shadowColor =
        if (enabled) green.copy(alpha = 0.6f)
        else Color(0xFF9CA69C)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)

    ) {

        // ---------- Bottom shadow ----------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(shadowColor)
        )

        // ---------- Top button ----------
        Button(
            onClick = {
                if (!enabled) return@Button

                scope.launch {

                    // press down
                    offsetY.animateTo(
                        6f,
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
            // ⭐ EXTRA PRO UPGRADE (compression effect)
            /* .graphicsLayer {
                 val pressed = offsetY.value > 0f
                 scaleX = if (pressed) 0.98f else 1f
                 scaleY = if (pressed) 0.98f else 1f
             }*/,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = buttonColor
            )
        ) {

            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (enabled) Color.White else Color(0xFF7A7A7A)
            )
        }
    }
}