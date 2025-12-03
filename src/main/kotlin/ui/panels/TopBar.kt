package ui.panels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.AppColors
import ui.ConnectionStatus
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopAppBar(
    connectionStatus: ConnectionStatus,
    lastCheck: LocalDateTime?,
    onConnectionCheck: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Icon(Icons.Default.Science, contentDescription = null)
                Spacer(Modifier.Companion.width(8.dp))
                Text("Technologia - Zarządzanie Próbkami")
            }
        },
        actions = {
            // Kontrolka połączenia
            TooltipArea(
                tooltip = {
                    Surface(
                        modifier = Modifier.Companion.shadow(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF424242)
                    ) {
                        Column(modifier = Modifier.Companion.padding(8.dp)) {
                            Text(
                                text = when (connectionStatus) {
                                    ConnectionStatus.CONNECTED -> "Połączono z bazą"
                                    ConnectionStatus.DISCONNECTED -> "Brak połączenia"
                                    ConnectionStatus.CHECKING -> "Sprawdzanie..."
                                },
                                color = Color.Companion.White,
                                fontSize = 11.sp
                            )
                            lastCheck?.let {
                                Text(
                                    text = "Ostatnie: ${it.toString().take(16)}",
                                    color = Color.Companion.LightGray,
                                    fontSize = 9.sp
                                )
                            }
                            Text(
                                text = "Kliknij aby odświeżyć",
                                color = Color.Companion.LightGray,
                                fontSize = 9.sp
                            )
                        }
                    }
                },
                delayMillis = 300
            ) {
                Surface(
                    modifier = Modifier.Companion
                        .size(32.dp)
                        .clickable { onConnectionCheck() }
                        .padding(8.dp),
                    shape = CircleShape,
                    color = when (connectionStatus) {
                        ConnectionStatus.CONNECTED -> AppColors.StatusCompleted // Zielony
                        ConnectionStatus.DISCONNECTED -> AppColors.Error // Czerwony
                        ConnectionStatus.CHECKING -> Color.Companion.Gray // Szary
                    }
                ) {
                    Box(contentAlignment = Alignment.Companion.Center) {
                        if (connectionStatus == ConnectionStatus.CHECKING) {
                            CircularProgressIndicator(
                                modifier = Modifier.Companion.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.Companion.White
                            )
                        }
                    }
                }
            }
        },
        backgroundColor = AppColors.Primary
    )
}