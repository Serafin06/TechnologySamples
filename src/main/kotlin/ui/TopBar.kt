package pl.rafapp.techSam.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopAppBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).padding(end = 8.dp)
                )
                Text("Technologia - Zarządzanie Próbkami", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        backgroundColor = AppColors.Primary,
        contentColor = Color.White,
        elevation = 4.dp
    )
}