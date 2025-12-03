package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun StatusBadge(stan: Byte, compact: Boolean = false) {
    Box(
        modifier = Modifier
            .size(if (compact) 12.dp else 16.dp)
            .clip(RoundedCornerShape(50))
            .background(getStatusColor(stan))
    )
}

fun getStatusColor(stan: Byte): Color {
    return when (stan.toInt()) {
        0 -> AppColors.StatusCompleted
        1 -> AppColors.StatusInProgress
        2 -> AppColors.StatusCancel
        3 -> AppColors.StatusPlaned
        4 -> AppColors.StatusCancelled
        else -> Color.Gray
    }
}