package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.StatusInfo


@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(
            "$label: ",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatusChip(label: String, status: StatusInfo?, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = status?.stan?.let { getStatusColor(it) } ?: Color.LightGray
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            if (status != null) {
                Text(
                    status.stanNazwa,
                    fontSize = 10.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                status.wykonana?.let { wyk ->
                    status.ilosc?.let { ilosc ->
                        val procent = ((wyk / ilosc) * 100).toInt()
                        Text(
                            "$procent%",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                Text("Brak", fontSize = 10.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun StatusBadge(stan: Byte, compact: Boolean = false) {
    Box(
        modifier = Modifier
            .size(if (compact) 12.dp else 16.dp)
            .clip(RoundedCornerShape(50))
            .background(getStatusColor(stan))
    )
}

@Composable
fun StatusDetails(label: String, status: StatusInfo) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(status.stan)
            Spacer(Modifier.width(8.dp))
            Text("$label: ${status.stanNazwa}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
        if (status.ilosc != null || status.wykonana != null) {
            Text(
                "  Wykonano: ${status.wykonana ?: 0} / ${status.ilosc ?: 0}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        status.terminZak?.let {
            Text("  Termin: ${it.toString().take(10)}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

fun getStatusColor(stan: Byte): Color {
    return when (stan.toInt()) {
        0 -> AppColors.StatusNew
        1 -> AppColors.StatusInProgress
        2 -> AppColors.StatusCompleted
        3 -> AppColors.StatusPaused
        4 -> AppColors.StatusCancelled
        else -> Color.Gray
    }
}