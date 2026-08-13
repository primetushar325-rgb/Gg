package com.example.smartedge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun EdgeHandleView(
    isRight: Boolean,
    opacity: Float,
    onSwipeToOpen: () -> Unit,
    onDragY: (Float) -> Unit
) {
    var totalDragX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(opacity)
            .clip(
                if (isRight) RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                else RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .background(Color.White.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { totalDragX = 0f },
                    onDragEnd = {
                        if (isRight && totalDragX < -40f) {
                            onSwipeToOpen()
                        } else if (!isRight && totalDragX > 40f) {
                            onSwipeToOpen()
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                    }
                )
            }
    )
}
