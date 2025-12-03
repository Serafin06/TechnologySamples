package ui.panels

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.AppColors

@Composable
fun LoadingScreen(progress: Float = 0f, message: String = "Ładowanie próbek...") {
    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            modifier = Modifier.Companion.width(300.dp)
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.Companion.height(24.dp))

            if (progress > 0f) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.Companion.fillMaxWidth(),
                    color = AppColors.Primary
                )
                Spacer(Modifier.Companion.height(8.dp))
                Text("${(progress * 100).toInt()}%", color = Color.Companion.Gray, fontSize = 14.sp)
            }

            Spacer(Modifier.Companion.height(16.dp))
            Text(message, fontWeight = FontWeight.Companion.Medium)
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = AppColors.Error,
                modifier = Modifier.Companion.size(64.dp)
            )
            Spacer(Modifier.Companion.height(16.dp))
            Text("Wystąpił błąd", fontWeight = FontWeight.Companion.Bold, fontSize = 18.sp)
            Spacer(Modifier.Companion.height(8.dp))
            Text(message, color = Color.Companion.Gray)
        }
    }
}

@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color.Companion.Gray,
                modifier = Modifier.Companion.size(64.dp)
            )
            Spacer(Modifier.Companion.height(16.dp))
            Text("Brak wyników", fontWeight = FontWeight.Companion.Bold, fontSize = 18.sp)
            Spacer(Modifier.Companion.height(8.dp))
            Text("Spróbuj zmienić filtry wyszukiwania", color = Color.Companion.Gray)
        }
    }
}