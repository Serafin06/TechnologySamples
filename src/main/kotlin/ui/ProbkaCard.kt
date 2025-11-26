package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.ProbkaDTO
import base.StatusInfo

/**
 * Kompaktowa karta próbki z możliwością edycji
 *
 * SEKCJE DO CUSTOMIZACJI:
 * 1. HEADER - wszystkie dane w jednej linii (linie 30-70)
 * 2. NAZWA PRÓBKI - opis1 (linia 72-80)
 * 3. STATUSY (lewo 2x2) + NOTATKI (prawo w kolumnach) (linie 82-220)
 * 4. SZCZEGÓŁY - rozwijane szczegółowe statusy (linie 222-270)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProbkaCard(
    probka: ProbkaDTO,
    onTechnologiaSave: ((String?, String?, String?, String?) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var notesExpanded by remember { mutableStateOf(false) }

    // Lokalne stany dla kolumn technologia
    var technologia1 by remember { mutableStateOf(probka.opis ?: "") }
    var technologia2 by remember { mutableStateOf(probka.dodtkoweInformacje ?: "") }
    var technologia3 by remember { mutableStateOf(probka.uwagi ?: "") }
    var technologia4 by remember { mutableStateOf(probka.testy ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // PADDING: główny padding karty

            // ═══════════════════════════════════════════════════════
            // 1️⃣ HEADER - Wszystkie dane w jednej linii
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lewa część - wszystkie dane techniczne w jednej linii
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp), // ODSTĘP: między elementami
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "#${probka.numer}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp // ROZMIAR: numer
                    )
                    Text(
                        probka.oddzialNazwa,
                        color = Color.Gray,
                        fontSize = 12.sp // ROZMIAR: oddział
                    )
                    Text(
                        probka.dataZamowienia.toString().take(10),
                        color = Color.Gray,
                        fontSize = 11.sp // ROZMIAR: data
                    )
                    probka.art?.let {
                        Text(it, fontSize = 12.sp, color = Color.DarkGray)
                    }
                    probka.receptura?.let {
                        Text(it, fontSize = 12.sp, color = Color.DarkGray)
                    }
                    probka.szerokosc?.let {
                        Text("${it}mm", fontSize = 11.sp, color = Color.DarkGray)
                    }
                    probka.statusZO?.let {
                        Text(
                            "${it.ilosc?.toInt() ?: 0}${probka.jm ?: ""}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                    // Grubości z jednostką
                    listOfNotNull(probka.grubosc11, probka.grubosc21, probka.grubosc31)
                        .filter { it.isNotBlank() }
                        .joinToString("/")
                        .takeIf { it.isNotBlank() }
                        ?.let {
                            Text("${it}μm", fontSize = 11.sp, color = Color.Gray) // ROZMIAR: grubości
                        }
                }

                // Prawa część - rozwijanie
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp) // ROZMIAR: przycisk
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Rozwiń",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // ═══════════════════════════════════════════════════════
            // 2️⃣ NAZWA PRÓBKI - nazwa
            // ═══════════════════════════════════════════════════════
            probka.nazwa?.let {
                Spacer(Modifier.height(4.dp)) // ODSTĘP: przed nazwą
                Text(
                    it,
                    fontSize = 12.sp, // ROZMIAR: nazwa próbki
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
            }

            Spacer(Modifier.height(8.dp)) // ODSTĘP: przed sekcją główną

            // ═══════════════════════════════════════════════════════
            // 3️⃣ STATUSY (lewo 2x2) + NOTATKI (prawo w kolumnach)
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // ODSTĘP: statusy-notatki
            ) {
                // LEWA STRONA - Statusy 2x2 (30%)
                Column(
                    modifier = Modifier.weight(0.2f),
                    verticalArrangement = Arrangement.spacedBy(4.dp) // ODSTĘP: między rzędami statusów
                ) {
                    // Rząd 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp) // ODSTĘP: między statusami w rzędzie
                    ) {
                        probka.statusZO?.let {
                            StatusBadge("Zlecenie", it, Modifier.weight(1f))
                        }
                        probka.statusZD?.let {
                            StatusBadge("Drukowanie", it, Modifier.weight(1f))
                        }
                    }

                    // Rząd 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // ZL - pokaż wszystkie zlecenia lub puste miejsce
                        if (probka.statusZL != null && probka.statusZL.isNotEmpty()) {
                            if (probka.statusZL.size == 1) {
                                StatusBadge("Laminacja", probka.statusZL[0], Modifier.weight(1f))
                            } else {
                                // 2 lub więcej zleceń - używamy ROW, żeby podzielić kostkę w poziomie
                                Row(
                                    modifier = Modifier.weight(1f), // Ta cała sekcja nadal zajmuje 50% głównego wiersza
                                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Dodajemy odstęp między mniejszymi kostkami
                                ) {
                                    // Używamy Modifier.weight(1f) na KAŻDYM elemencie, żeby podzielić szerokość ROW po równo
                                    probka.statusZL.take(2).forEachIndexed { index, status ->
                                        StatusBadge(
                                            "Laminacja ${index + 1}",
                                            status,
                                            Modifier.weight(1f) // Dzieli wewnętrzny Row na pół (50% / 50%)
                                        )
                                    }
                                }
                            }
                        }

                        probka.statusZK?.let {
                            StatusBadge("Krajarki", it, Modifier.weight(1f))
                        }
                    }
                }

                // PRAWA STRONA - Notatki w kolumnach (70%)
                Column(modifier = Modifier.weight(0.7f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Notatki technologiczne",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp, // ROZMIAR: tytuł
                            color = Color.Gray
                        )

                        Row {
                            if (!editMode) {
                                IconButton(
                                    onClick = { notesExpanded = !notesExpanded },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        if (notesExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                                        contentDescription = "Rozwiń notatki",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            if (onTechnologiaSave != null) {
                                IconButton(
                                    onClick = { editMode = !editMode },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        if (editMode) Icons.Default.Close else Icons.Default.Edit,
                                        contentDescription = "Edytuj",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    if (editMode && onTechnologiaSave != null) {
                        // TRYB EDYCJI - 2 kolumny
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp) // ODSTĘP: między polami
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = technologia1,
                                    onValueChange = { technologia1 = it },
                                    label = { Text("Opis", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = false,
                                    maxLines = 2,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp) // ROZMIAR: tekst pola
                                )

                                OutlinedTextField(
                                    value = technologia2,
                                    onValueChange = { technologia2 = it },
                                    label = { Text("Dodatkowe informacje", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = false,
                                    maxLines = 2,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = technologia3,
                                    onValueChange = { technologia3 = it },
                                    label = { Text("Uwagi", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = false,
                                    maxLines = 2,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
                                )

                                OutlinedTextField(
                                    value = technologia4,
                                    onValueChange = { technologia4 = it },
                                    label = { Text("Testy", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = false,
                                    maxLines = 2,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
                                )
                            }

                            Button(
                                onClick = {
                                    onTechnologiaSave(
                                        technologia1.ifBlank { null },
                                        technologia2.ifBlank { null },
                                        technologia3.ifBlank { null },
                                        technologia4.ifBlank { null }
                                    )
                                    editMode = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp) // PADDING: przycisk
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Zapisz", fontSize = 10.sp)
                            }
                        }
                    } else {
                        // TRYB PODGLĄDU - 2 kolumny z tooltipami
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp) // ODSTĘP: między notatkami
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp) // ODSTĘP: między kolumnami
                            ) {
                                NoteWithTooltip("Opis", probka.opis ?: "-", notesExpanded, Modifier.weight(1f))
                                NoteWithTooltip(
                                    "Dodatkowe informacje",
                                    probka.dodtkoweInformacje ?: "-",
                                    notesExpanded,
                                    Modifier.weight(1f)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NoteWithTooltip("Uwagi", probka.uwagi ?: "-", notesExpanded, Modifier.weight(1f))
                                NoteWithTooltip("Testy", probka.testy ?: "-", notesExpanded, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ═══════════════════════════════════════════════════════
            // 4️⃣ SZCZEGÓŁY - Rozwijane szczegółowe statusy
            // ═══════════════════════════════════════════════════════
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                Text(
                    "Szczegóły statusów",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))

                // Szczegółowe statusy z ilościami
                probka.statusZO?.let { StatusDetailsExpanded("Zlecenie", it) }
                probka.statusZD?.let { StatusDetailsExpanded("Drukowanie", it) }

                if (probka.statusZL != null && probka.statusZL.isNotEmpty()) {
                    probka.statusZL.forEachIndexed { index, status ->
                        StatusDetailsExpanded("Laminacja ${index + 1}", status)
                    }
                }

                probka.statusZK?.let { StatusDetailsExpanded("Krajarki", it) }
            }
        }
    }
}

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