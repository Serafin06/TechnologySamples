package pl.rafapp.techSam.UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import pl.rafapp.techSam.Base.ProbkaDTO
import pl.rafapp.techSam.Base.StatusInfo

/**
 * Kompaktowa karta próbki z możliwością edycji
 *
 * SEKCJE DO CUSTOMIZACJI:
 * 1. HEADER - numer, oddział, data, ART, receptura, szer, ilosc, grubości (linie 30-80)
 * 2. NAZWA PRÓBKI - opis1 (linia 82-90)
 * 3. STATUSY + NOTATKI - statusy po prawej, notatki po lewej (linie 92-200)
 * 4. SZCZEGÓŁY - rozwijane info ze szczegółowymi statusami (linie 202-250)
 */
@Composable
fun ProbkaCard(
    probka: ProbkaDTO,
    onTechnologiaSave: ((String?, String?, String?, String?) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }

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
            // 1️⃣ HEADER - Kompaktowy z wszystkimi danymi technicznymi
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lewa część - dane techniczne
                Column(modifier = Modifier.weight(1f)) {
                    // Linia 1: Numer, Oddział, Data
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp) // ODSTĘP: między elementami
                    ) {
                        Text(
                            "#${probka.numer}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp // ROZMIAR: numer zamówienia
                        )
                        Text(
                            probka.oddzialNazwa,
                            color = Color.Gray,
                            fontSize = 13.sp // ROZMIAR: oddział
                        )
                        Text(
                            probka.dataZamowienia.toString().take(10),
                            color = Color.Gray,
                            fontSize = 12.sp // ROZMIAR: data
                        )
                    }

                    Spacer(Modifier.height(6.dp)) // ODSTĘP: między liniami danych

                    // Linia 2: ART, Receptura, Szerokość, Ilość, Grubości
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp) // ODSTĘP: między danymi
                    ) {
                        probka.art?.let {
                            Text(
                                it,
                                fontSize = 12.sp, // ROZMIAR: ART
                                color = Color.DarkGray
                            )
                        }
                        probka.receptura?.let {
                            Text(
                                it,
                                fontSize = 12.sp, // ROZMIAR: receptura
                                color = Color.DarkGray
                            )
                        }
                        probka.szerokosc?.let {
                            Text(
                                "${it}mm",
                                fontSize = 12.sp, // ROZMIAR: szerokość
                                color = Color.DarkGray
                            )
                        }
                        probka.statusZO?.let {
                            Text(
                                "${it.ilosc?.toInt() ?: 0} ${probka.jm ?: ""}",
                                fontSize = 12.sp, // ROZMIAR: ilość
                                color = Color.DarkGray
                            )
                        }
                        // Grubości
                        probka.grubosc11?.let {
                            Text(
                                it,
                                fontSize = 11.sp, // ROZMIAR: grubości
                                color = Color.Gray
                            )
                        }
                        probka.grubosc21?.let {
                            Text(
                                it,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        probka.grubosc31?.let {
                            Text(
                                it,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Prawa część - akcje
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp) // ROZMIAR: przycisk rozwijania
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Rozwiń",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ═══════════════════════════════════════════════════════
            // 2️⃣ NAZWA PRÓBKI - opis1
            // ═══════════════════════════════════════════════════════
            probka.nazwa?.let {
                Spacer(Modifier.height(6.dp)) // ODSTĘP: przed nazwą próbki
                Text(
                    it,
                    fontSize = 13.sp, // ROZMIAR: nazwa próbki
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
            }

            Spacer(Modifier.height(10.dp)) // ODSTĘP: przed sekcją statusów i notatek

            // ═══════════════════════════════════════════════════════
            // 3️⃣ STATUSY (prawo) + NOTATKI TECHNOLOGICZNE (lewo)
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // ODSTĘP: między statusami a notatkami
            ) {
                // LEWA STRONA - Notatki technologiczne (70% szerokości)
                Column(modifier = Modifier.weight(0.7f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Notatki",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp, // ROZMIAR: tytuł sekcji
                            color = Color.Gray
                        )

                        if (onTechnologiaSave != null) {
                            IconButton(
                                onClick = { editMode = !editMode },
                                modifier = Modifier.size(28.dp) // ROZMIAR: przycisk edycji
                            ) {
                                Icon(
                                    if (editMode) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = "Edytuj",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    if (editMode && onTechnologiaSave != null) {
                        // TRYB EDYCJI
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp) // ODSTĘP: między polami edycji
                        ) {
                            OutlinedTextField(
                                value = technologia1,
                                onValueChange = { technologia1 = it },
                                label = { Text("Uwagi 1", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp) // ROZMIAR: tekst w polu
                            )

                            OutlinedTextField(
                                value = technologia2,
                                onValueChange = { technologia2 = it },
                                label = { Text("Uwagi 2", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                            )

                            OutlinedTextField(
                                value = technologia3,
                                onValueChange = { technologia3 = it },
                                label = { Text("Uwagi 3", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                            )

                            OutlinedTextField(
                                value = technologia4,
                                onValueChange = { technologia4 = it },
                                label = { Text("Uwagi 4", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                            )

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
                                contentPadding = PaddingValues(vertical = 8.dp) // PADDING: przycisk zapisu
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Zapisz", fontSize = 11.sp)
                            }
                        }
                    } else {
                        // TRYB PODGLĄDU
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp) // ODSTĘP: między notatkami
                        ) {
                            CompactNote("1", probka.opis ?: "-")
                            CompactNote("2", probka.dodtkoweInformacje ?: "-")
                            CompactNote("3", probka.uwagi ?: "-")
                            CompactNote("4", probka.testy ?: "-")
                        }
                    }
                }

                // PRAWA STRONA - Statusy (30% szerokości)
                Column(
                    modifier = Modifier.weight(0.3f),
                    verticalArrangement = Arrangement.spacedBy(6.dp) // ODSTĘP: między statusami
                ) {
                    Text(
                        "Statusy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp, // ROZMIAR: tytuł sekcji
                        color = Color.Gray
                    )

                    // Statusy jako duże kostki z kontrastem
                    StatusBadge("ZO", probka.statusZO)
                    StatusBadge("ZD", probka.statusZD)

                    // ZL - pokaż wszystkie zlecenia
                    if (probka.statusZL != null && probka.statusZL.isNotEmpty()) {
                        probka.statusZL.forEachIndexed { index, status ->
                            StatusBadge("ZL${index + 1}", status)
                        }
                    } else {
                        StatusBadge("ZL", null)
                    }

                    StatusBadge("ZK", probka.statusZK)
                }
            }

            // ═══════════════════════════════════════════════════════
            // 4️⃣ SZCZEGÓŁY - Rozwijane szczegółowe statusy
            // ═══════════════════════════════════════════════════════
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                Text(
                    "Szczegóły statusów",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(8.dp))

                // Szczegółowe statusy z ilościami
                probka.statusZO?.let { StatusDetailsExpanded("ZO", it) }
                probka.statusZD?.let { StatusDetailsExpanded("ZD", it) }

                if (probka.statusZL != null && probka.statusZL.isNotEmpty()) {
                    probka.statusZL.forEachIndexed { index, status ->
                        StatusDetailsExpanded("ZL${index + 1}", status)
                    }
                }

                probka.statusZK?.let { StatusDetailsExpanded("ZK", it) }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 🧩 Komponenty pomocnicze
// ═══════════════════════════════════════════════════════════════

/**
 * Kompaktowa notatka - numer + tekst
 * STYL: zmień rozmiary, kolory
 */
@Composable
fun CompactNote(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(4.dp) // ODSTĘP: numer-tekst
    ) {
        Text(
            "$number.",
            fontSize = 10.sp, // ROZMIAR: numer notatki
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            text,
            fontSize = 11.sp, // ROZMIAR: tekst notatki
            maxLines = 2,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Duża kostka statusu z kontrastem - tylko kolor + nazwa
 * WYGLĄD: zmień kolory, zaokrąglenia, padding dla efektu wizualnego
 */
@Composable
fun StatusBadge(label: String, status: StatusInfo?) {
    val (color, bgAlpha) = when (status?.stan) {
        0.toByte() -> Color(0xFF4CAF50) to 0.25f // Wykonane - mocny zielony
        1.toByte() -> Color(0xFF2196F3) to 0.25f // W realizacji - mocny niebieski
        2.toByte() -> Color(0xFFFF9800) to 0.25f // Planowane - mocny pomarańczowy
        3.toByte() -> Color(0xFFFF5722) to 0.25f // Wstrzymane - czerwony
        else -> Color.Gray to 0.15f // Brak/nieznany
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp), // ZAOKRĄGLENIE: kostki statusu
        color = color.copy(alpha = bgAlpha)
    ) {
        Column(
            modifier = Modifier.padding(8.dp), // PADDING: wewnątrz kostki statusu
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                fontSize = 11.sp, // ROZMIAR: etykieta statusu
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(Modifier.height(2.dp))
            Text(
                status?.stanNazwa ?: "Brak",
                fontSize = 9.sp, // ROZMIAR: nazwa statusu
                color = color,
                maxLines = 1
            )
        }
    }
}

/**
 * Rozwinięte szczegóły statusu z ilościami
 * ZAWARTOŚĆ: zmień jakie dane wyświetlać
 */
@Composable
fun StatusDetailsExpanded(label: String, status: StatusInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp) // PADDING: odstęp między statusami
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "$label: ${status.stanNazwa}",
                fontSize = 12.sp, // ROZMIAR: nazwa w szczegółach
                fontWeight = FontWeight.Medium
            )
            Text(
                "${status.wykonana?.toInt() ?: 0} / ${status.ilosc?.toInt() ?: 0}",
                fontSize = 12.sp, // ROZMIAR: ilości w szczegółach
                color = Color.Gray
            )
        }

        status.terminZak?.let {
            Spacer(Modifier.height(2.dp))
            Text(
                "Termin: ${it.toString().take(10)}",
                fontSize = 10.sp, // ROZMIAR: termin
                color = Color.Gray
            )
        }

        status.dataZak?.let {
            Text(
                "Zakończono: ${it.toString().take(10)}",
                fontSize = 10.sp, // ROZMIAR: data zakończenia
                color = Color.Gray
            )
        }
    }
}