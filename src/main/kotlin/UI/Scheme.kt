package pl.rafapp.techSam.UI

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

// 🎨 Theme & Colors

object AppColors {
    val Primary = Color(0xFF2196F3)
    val Secondary = Color(0xFF03DAC6)
    val Background = Color.LightGray
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
    val stanZL: Byte? = null,
    val dateRange: DateRange = DateRange.SIX_MONTHS,
    val customDateFrom: LocalDateTime? = null,
    val customDateTo: LocalDateTime? = null
)

enum class DateRange(val label: String, val months: Long?) {
    THREE_MONTHS("Ostatnie 3 miesiące", 3),
    SIX_MONTHS("Ostatnie 6 miesięcy", 6),
    ONE_YEAR("Ostatni rok", 12),
    CUSTOM("Własny zakres", null)
}