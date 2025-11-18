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
 * 1. HEADER - numer, oddział, data (linie 30-50)
 * 2. DANE TECHNICZNE - receptura, grubości, szerokość (linie 52-70)
 * 3. STATUSY - ZO, ZK, ZD, ZL w jednej linii (linie 72-85)
 * 4. TODO KOLUMNY - 4 edytowalne pola (linie 87-120)
 * 5. SZCZEGÓŁY - rozwijane info (linie 122-150)
 */
@Composable
fun ProbkaCard(
    probka: ProbkaDTO,
    onTechnologiaSave: ((String?, String?, String?, String?) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }

    // Lokalne stany dla TODO kolumn
    var technologia1 by remember { mutableStateOf(probka.opis ?: "") }
    var technologia2 by remember { mutableStateOf(probka.dodtkoweInformacje ?: "") }
    var technologia3 by remember { mutableStateOf(probka.uwagi ?: "") }
    var technologia4 by remember { mutableStateOf(probka.testy ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // PADDING: zmniejsz/zwiększ dla kompaktowości

            // ═══════════════════════════════════════════════════════
            // 1️⃣ HEADER - Numer, Oddział, Data
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lewa część - główne info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "#${probka.numer}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp // ROZMIAR: zmień dla większej/mniejszej czcionki
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        probka.oddzialNazwa,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        probka.dataZamowienia.toString().take(10),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Prawa część - akcje
                Row {
                    if (onTechnologiaSave != null) {
                        IconButton(
                            onClick = { editMode = !editMode },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (editMode) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = "Edytuj",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Rozwiń",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp)) // ODSTĘP: zmień dla większej/mniejszej przerwy

            // ═══════════════════════════════════════════════════════
            // 2️⃣ DANE TECHNICZNE - Receptura, ART, Grubości
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // ODSTĘP: między elementami
            ) {
                probka.art?.let {
                    CompactInfo("ART", it, Modifier.weight(1f))
                }
                probka.receptura?.let {
                    CompactInfo("Receptura", it, Modifier.weight(1f))
                }
                probka.szerokosc?.let {
                    CompactInfo("Szer.", "${it}mm", Modifier.weight(0.7f))
                }
            }

            Spacer(Modifier.height(8.dp))

            // ═══════════════════════════════════════════════════════
            // 3️⃣ STATUSY - ZO, ZK, ZD, ZL w kompaktowej formie
            // ═══════════════════════════════════════════════════════
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MiniStatusChip("ZO", probka.statusZO, Modifier.weight(1f))
                MiniStatusChip("ZD", probka.statusZD, Modifier.weight(1f))

                // ZL - pokaż wszystkie zlecenia lub "-"
                if (probka.statusZL != null && probka.statusZL.isNotEmpty()) {
                    Column(modifier = Modifier.weight(1f)) {
                        probka.statusZL.forEachIndexed { index, status ->
                            MiniStatusChip("ZL${index + 1}", status, Modifier.fillMaxWidth())
                            if (index < probka.statusZL.size - 1) Spacer(Modifier.height(4.dp))
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Gray.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ZL: -", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                MiniStatusChip("ZK", probka.statusZK, Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            // ═══════════════════════════════════════════════════════
            // 4️⃣ KOLUMNY - Edytowalne pola (opcjonalne)
            // ═══════════════════════════════════════════════════════
            Divider()
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Notatki technologiczne",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                if (onTechnologiaSave != null) {
                    IconButton(
                        onClick = { editMode = !editMode },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (editMode) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edytuj",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (editMode && onTechnologiaSave != null) {
                // Tryb edycji - pola tekstowe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = technologia1,
                        onValueChange = { technologia1 = it },
                        label = { Text("Uwagi 1") },
                        modifier = Modifier.weight(1f),
                        singleLine = false,
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = technologia2,
                        onValueChange = { technologia2 = it },
                        label = { Text("Uwagi 2") },
                        modifier = Modifier.weight(1f),
                        singleLine = false,
                        maxLines = 3
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = technologia3,
                        onValueChange = { technologia3 = it },
                        label = { Text("Uwagi 3") },
                        modifier = Modifier.weight(1f),
                        singleLine = false,
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = technologia4,
                        onValueChange = { technologia4 = it },
                        label = { Text("Uwagi 4") },
                        modifier = Modifier.weight(1f),
                        singleLine = false,
                        maxLines = 3
                    )
                }

                Spacer(Modifier.height(8.dp))

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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Zapisz")
                }
            } else {
                // Tryb podglądu - tylko tekst
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Uwagi 1:", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            probka.opis ?: "-",
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Uwagi 2:", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            probka.dodtkoweInformacje ?: "-",
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Uwagi 3:", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            probka.uwagi ?: "-",
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Uwagi 4:", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            probka.testy ?: "-",
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            // ═══════════════════════════════════════════════════════
            // 5️⃣ SZCZEGÓŁY - Rozwijane dodatkowe info
            // ═══════════════════════════════════════════════════════
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                Text(
                    "Szczegóły techniczne",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(6.dp))

                // Grubości
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailItem("Grubość 1", probka.grubosc11 ?: "-")
                    DetailItem("Grubość 2", probka.grubosc21 ?: "-")
                    DetailItem("Grubość 3", probka.grubosc31 ?: "-")
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    "Szczegóły statusów",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(6.dp))

                // Rozwinięte statusy
                probka.statusZO?.let { status -> StatusDetails("ZO", status) }
                probka.statusZK?.let { status -> StatusDetails("ZK", status) }
                probka.statusZD?.let { status -> StatusDetails("ZD", status) }
                probka.statusZL?.firstOrNull()?.let { status -> StatusDetails("ZL", status) }

            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 🧩 Komponenty pomocnicze
// ═══════════════════════════════════════════════════════════════

    /**
     * Kompaktowe info - label + wartość w jednej linii
     * STYL: zmień kolory, rozmiary czcionek
     */
    @Composable
    fun CompactInfo(label: String, value: String, modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Text(
                label,
                fontSize = 10.sp, // ROZMIAR: etykieta
                color = Color.Gray
            )
            Text(
                value,
                fontSize = 13.sp, // ROZMIAR: wartość
                fontWeight = FontWeight.Medium
            )
        }
    }


    /**
     * Mini chip statusu - bardzo kompaktowy
     * WYGLĄD: zmień kolory, padding, zaokrąglenia
     */
    @Composable
    fun MiniStatusChip(label: String, status: StatusInfo?, modifier: Modifier = Modifier) {
        val color = when (status?.stan) {
            0.toByte() -> Color(0xFF4CAF50) // Wykonane - zielony
            1.toByte() -> Color(0xFF2196F3) // W realizacji - niebieski
            2.toByte() -> Color(0xFFFF9800) // Planowane - pomarańczowy
            else -> Color.Gray
        }

        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(4.dp), // ZAOKRĄGLENIE: zmień dla bardziej/mniej zaokrąglonych
            color = color.copy(alpha = 0.1f)
        ) {
            Column(
                modifier = Modifier.padding(6.dp), // PADDING: wewnętrzny odstęp
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    label,
                    fontSize = 10.sp, // ROZMIAR: etykieta
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                status?.let {
                    Text(
                        "${it.wykonana?.toInt() ?: 0}/${it.ilosc?.toInt() ?: 0}",
                        fontSize = 9.sp, // ROZMIAR: postęp
                        color = color
                    )
                }
            }
        }
    }

    /**
     * Szczegółowy element - dla rozwijalnej sekcji
     */
    @Composable
    fun DetailItem(label: String, value: String) {
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 12.sp)
        }
    }


