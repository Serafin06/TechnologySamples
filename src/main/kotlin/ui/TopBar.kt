package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopAppBar(
    connectionStatus: ConnectionStatus,
    lastCheck: java.time.LocalDateTime?,
    onConnectionCheck: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Science, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Technologia - Zarządzanie Próbkami")
            }
        },
        actions = {
            // Kontrolka połączenia
            TooltipArea(
                tooltip = {
                    Surface(
                        modifier = Modifier.shadow(4.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        color = Color(0xFF424242)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = when (connectionStatus) {
                                    ConnectionStatus.CONNECTED -> "Połączono z bazą"
                                    ConnectionStatus.DISCONNECTED -> "Brak połączenia"
                                    ConnectionStatus.CHECKING -> "Sprawdzanie..."
                                },
                                color = Color.White,
                                fontSize = 11.sp
                            )
                            lastCheck?.let {
                                Text(
                                    text = "Ostatnie: ${it.toString().take(16)}",
                                    color = Color.LightGray,
                                    fontSize = 9.sp
                                )
                            }
                            Text(
                                text = "Kliknij aby odświeżyć",
                                color = Color.LightGray,
                                fontSize = 9.sp
                            )
                        }
                    }
                },
                delayMillis = 300
            ) {
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onConnectionCheck() }
                        .padding(8.dp),
                    shape = CircleShape,
                    color = when (connectionStatus) {
                        ConnectionStatus.CONNECTED -> Color(0xFF4CAF50) // Zielony
                        ConnectionStatus.DISCONNECTED -> Color(0xFFF44336) // Czerwony
                        ConnectionStatus.CHECKING -> Color.Gray // Szary
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (connectionStatus == ConnectionStatus.CHECKING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        backgroundColor = AppColors.Primary
    )
}