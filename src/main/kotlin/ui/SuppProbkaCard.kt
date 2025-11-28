package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.StatusInfo

// ═══════════════════════════════════════════════════════════════
// 🧩 Komponenty pomocnicze
// ═══════════════════════════════════════════════════════════════

/**
 * Notatka z tooltipem - rozwija się lub pokazuje pełny tekst przy najechaniu
 * FUNKCJONALNOŚĆ: zmień maxLines dla domyślnego widoku
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteWithTooltip(title: String, text: String, expanded: Boolean, modifier: Modifier = Modifier) {
    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF424242)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        delayMillis = 300 // DELAY: opóźnienie pokazania tooltipa (ms)
    ) {
        Column(
            modifier = modifier, // Używamy przekazanego Modifier (np. Modifier.weight(1f))
            verticalArrangement = Arrangement.spacedBy(1.dp) // Minimalny odstęp między nagłówkiem a treścią
        ) {
            // 1. NAGŁÓWEK (Twoje pole 'number')
            Text(
                text = title, // Wyświetlamy nagłówek, np. "Dodatkowe informacje:"
                fontSize = 9.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                // --- KOREKTA ZAPOBIEGAJĄCA ŁAMANIU W PIONIE ---
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
                // ---------------------------------------------
            )

            // 2. TREŚĆ NOTATKI (Twoje pole 'text')
            Text(
                text = text,
                fontSize = 10.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                // Usunąłem Modifier.weight(1f), ponieważ jest to już w głównym Column
            )
        }
    }
}


/**
 * Kostka statusu - mała, kompaktowa, tylko z tekstem
 * WYGLĄD: zmień kolory, zaokrąglenia, padding
 */
@Composable
fun StatusBadge(label: String, status: StatusInfo, modifier: Modifier = Modifier) {
    val (color, bgAlpha) = when (status.stan) {
        0.toByte() -> Color(0xFF4CAF50) to 0.25f // Wykonane - zielony
        1.toByte() -> Color(0xFF2196F3) to 0.25f // W realizacji - niebieski
        2.toByte() -> Color(0xFFFF9800) to 0.25f // Planowane - pomarańczowy
        3.toByte() -> Color(0xFFFF5722) to 0.25f // Wstrzymane - czerwony
        else -> Color.Gray to 0.15f
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp), // ZAOKRĄGLENIE: kostki
        color = color.copy(alpha = bgAlpha)
    ) {
        Column(
            modifier = Modifier.padding(6.dp), // PADDING: wewnątrz kostki (mniejszy)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontSize = 9.sp, // ROZMIAR: etykieta
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                status.stanNazwa,
                fontSize = 8.sp, // ROZMIAR: status
                color = color,
                maxLines = 1
            )
        }
    }
}

/**
 * Rozwinięte szczegóły statusu z ilościami
 * ZAWARTOŚĆ: dostosuj wyświetlane informacje
 */
@Composable
fun StatusDetailsExpanded(label: String, status: StatusInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // PADDING: odstęp statusów
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "$label: ${status.stanNazwa}",
                fontSize = 11.sp, // ROZMIAR: nazwa
                fontWeight = FontWeight.Medium
            )
            Text(
                "${status.wykonana?.toInt() ?: 0} / ${status.ilosc?.toInt() ?: 0}",
                fontSize = 11.sp, // ROZMIAR: ilości
                color = Color.Gray
            )
        }

        status.terminZak?.let {
            Text(
                "Termin: ${it.toString().take(10)}",
                fontSize = 9.sp, // ROZMIAR: termin
                color = Color.Gray
            )
        }

        status.dataZak?.let {
            Text(
                "Zakończono: ${it.toString().take(10)}",
                fontSize = 9.sp, // ROZMIAR: data zakończenia
                color = Color.Gray
            )
        }
    }
}
/**
 * Wskaźnik flagi - mała kostka z literą
 * KOLORY: 🟢 true (zielony), 🔴 false (czerwony), ⚫ null (szary)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlagIndicator(
    label: String,
    state: Boolean?,
    enabled: Boolean,
    tooltip: String,
    onClick: (() -> Unit)? = null
) {
    val color = when (state) {
        true -> Color(0xFF4CAF50)   // Zielony
        false -> Color(0xFFF44336)  // Czerwony
        null -> Color.Gray          // Szary
    }

    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF424242)
            ) {
                Text(
                    text = tooltip,
                    modifier = Modifier.padding(6.dp),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        },
        delayMillis = 300
    ) {
        Surface(
            modifier = Modifier
                .size(24.dp) // ROZMIAR: wielkość kostki flagi
                .then(
                    if (enabled && onClick != null) {
                        Modifier.clickable { onClick() }
                    } else Modifier
                ),
            shape = RoundedCornerShape(4.dp), // ZAOKRĄGLENIE: flagi
            color = color.copy(alpha = 0.3f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    label,
                    fontSize = 12.sp, // ROZMIAR: litera
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}