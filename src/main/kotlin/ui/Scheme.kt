package ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

// 🎨 Theme & Colors

object AppColors {
    val Primary = Color(0xFF2196F3)
    val Secondary = Color(0xFF03DAC6)
    val Background = Color.LightGray
    val Surface = Color.White
    val Error = Color(0xFFB00020)

    val StatusCompleted = Color(0xFF4CAF50)
    val StatusInProgress = Color(0xFF2196F3)
    val StatusCancel = Color(0xFF9E9E9E)
    val StatusPlaned = Color(0xFFFF9800)
    val StatusCancelled = Color(0xFFF44336)
}

// 🔍 Filter State

data class FilterState(
    val searchQuery: String = "",
    val oddzial: String? = "Tychy",

    val selectedStatusZO: Set<Byte> = emptySet(),
    val selectedStatusZK: Set<Byte> = emptySet(),
    val selectedStatusZD: Set<Byte> = emptySet(),
    val selectedStatusZL: Set<Byte> = emptySet(),
    val selectedKontrahenci: Set<String> = emptySet(),

    val selectedProduce: Set<Boolean?> = setOf(true, false), // null, true, false
    val selectedSend: Set<Boolean?> = emptySet(),
    val selectedTested: Set<Boolean?> = emptySet(),

    val dateRange: DateRange = DateRange.SIX_MONTHS,
    val customDateFrom: LocalDateTime? = null,
    val customDateTo: LocalDateTime? = null
){
    companion object {
        /**
         * Tworzy stan, w którym wszystkie filtry są wyczyszczone (nieaktywne).
         * Ten stan może się różnić od stanu domyślnego.
         */
        fun cleared(): FilterState {
            return FilterState(
                // Wymieniamy tylko te pola, które w stanie "czystym" mają mieć inną wartość niż domyślna.
                // Pozostałe pola przyjmą wartości domyślne z konstruktora.
                oddzial = null, // Założenie: "czysty" stan to brak selekcji oddziału (pokaż wszystkie)
                selectedProduce = emptySet() // Kluczowa zmiana: usuwamy preselekcję
            )
        }
    }
}

enum class DateRange(val label: String, val months: Long?) {
    THREE_MONTHS("Ostatnie 3 miesiące", 3),
    SIX_MONTHS("Ostatnie 6 miesięcy", 6),
    ONE_YEAR("Ostatni rok", 12),
    CUSTOM("Własny zakres", null)
}

val heightCell = 59.dp