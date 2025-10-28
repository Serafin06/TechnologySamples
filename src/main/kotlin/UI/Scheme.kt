package pl.rafapp.techSam.UI

import androidx.compose.ui.graphics.Color

// 🎨 Theme & Colors

object AppColors {
    val Primary = Color(0xFF2196F3)
    val Secondary = Color(0xFF03DAC6)
    val Background = Color(0xFFF5F5F5)
    val Surface = Color.White
    val Error = Color(0xFFB00020)

    val StatusNew = Color(0xFF4CAF50)
    val StatusInProgress = Color(0xFF2196F3)
    val StatusCompleted = Color(0xFF9E9E9E)
    val StatusPaused = Color(0xFFFF9800)
    val StatusCancelled = Color(0xFFF44336)
}

// 🔍 Filter State

data class FilterState(
    val searchQuery: String = "",
    val oddzial: String? = null,
    val stanZO: Byte? = null,
    val stanZK: Byte? = null,
    val stanZD: Byte? = null,
    val dateFrom: String = "",
    val dateTo: String = ""
)