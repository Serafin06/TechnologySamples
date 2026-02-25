package ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ui.AppColors
import base.ProbkaDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddMagazynDialog(
    foundProbka: ProbkaDTO?,
    isSearching: Boolean,
    searchError: String?,
    onSearch: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Int, String?, String?, String?, String?, String?, LocalDateTime?) -> Unit
) {
    var numerInput by remember { mutableStateOf("") }
    var strukturaMag by remember { mutableStateOf("") }
    var skladMag by remember { mutableStateOf("") }
    var szerokoscMag by remember { mutableStateOf("") }
    var iloscMag by remember { mutableStateOf("") }
    var uwagiMag by remember { mutableStateOf("") }
    var dataProdukcji by remember { mutableStateOf("") }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Automatyczne uzupełnianie, gdy ViewModel znajdzie próbkę
    LaunchedEffect(foundProbka) {
        foundProbka?.let { probka ->
            // Uzupełniamy skład z receptury (zgodnie z Twoim wymogiem)
            if (skladMag.isBlank()) {
                skladMag = probka.receptura ?: ""
            }
            val layers = listOfNotNull(
                probka.grubosc11?.takeIf { it.isNotBlank() },
                probka.grubosc21?.takeIf { it.isNotBlank() },
                probka.grubosc31?.takeIf { it.isNotBlank() }
            )
            if (layers.isNotEmpty() && strukturaMag.isBlank()) {
                strukturaMag = layers.joinToString("/")
            }
            if (dataProdukcji.isBlank()) {
                probka.statusZK?.dataZak?.let { dataProdukcji = it.format(dateFormatter) }
            }
        }
    }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Dodaj próbkę do magazynu") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Sekcja wyszukiwania
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = numerInput,
                            onValueChange = { numerInput = it },
                            label = { Text("Numer ZO") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { if (numerInput.isNotBlank()) onSearch(numerInput) }
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onSearch(numerInput) },
                            enabled = !isSearching && numerInput.isNotBlank()
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Szukaj")
                            }
                        }
                    }

                    // Wyświetlanie błędu szukania
                    if (searchError != null && foundProbka == null) {
                        Text(searchError, color = AppColors.Error, style = MaterialTheme.typography.caption)
                    }

                    // Wyświetlanie informacji o znalezionej próbce
                    if (foundProbka != null) {
                        Card(
                            backgroundColor = AppColors.Surface,
                            elevation = 2.dp,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Znaleziono:", style = MaterialTheme.typography.caption, color = AppColors.Primary)
                                Text(
                                    "Kontrahent: ${foundProbka.kontrahentNazwa}",
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                // Art usunięty zgodnie z wymogiem, ale można go wyświetlić dla info
                                Text("Art: ${foundProbka.art ?: "-"}")
                                Text("Receptura: ${foundProbka.receptura ?: "-"}")
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Pola edycji
                    OutlinedTextField(
                        value = skladMag,
                        onValueChange = { skladMag = it },
                        label = { Text("Skład (Receptura)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = strukturaMag,
                        onValueChange = { strukturaMag = it },
                        label = { Text("Struktura") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = szerokoscMag,
                        onValueChange = { szerokoscMag = it },
                        label = { Text("Szerokość") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = iloscMag,
                        onValueChange = { iloscMag = it },
                        label = { Text("Ilość") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uwagiMag,
                        onValueChange = { uwagiMag = it },
                        label = { Text("Uwagi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = dataProdukcji,
                            onValueChange = { dataProdukcji = it },
                            label = { Text("Data produkcji (yyyy-MM-dd)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Wybierz datę")
                        }
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            onDateSelected = { date ->
                                dataProdukcji = date.format(dateFormatter)
                                showDatePicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val numer = numerInput.toIntOrNull() ?: return@Button
                        val data = try {
                            if (dataProdukcji.isBlank()) null
                            else LocalDate.parse(dataProdukcji).atStartOfDay()
                        } catch (e: Exception) {
                            null
                        }

                        onConfirm(
                            numer, strukturaMag.ifBlank { null }, skladMag.ifBlank { null },
                            szerokoscMag.ifBlank { null }, iloscMag.ifBlank { null },
                            uwagiMag.ifBlank { null }, data
                        )
                    },
                    // Można dodać warunek: enabled = foundProbka != null, jeśli dodawać można tylko istniejące ZO
                    enabled = numerInput.toIntOrNull() != null && !isSearching
                ) { Text("Dodaj") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Anuluj") }
            }
        )
    }

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Wybierz datę") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Rok i miesiąc
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { selectedDate = selectedDate.minusMonths(1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                    Text(
                        "${selectedDate.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("pl"))} ${selectedDate.year}",
                        style = MaterialTheme.typography.subtitle1
                    )
                    IconButton(onClick = { selectedDate = selectedDate.plusMonths(1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Dni tygodnia
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Pn","Wt","Śr","Cz","Pt","So","Nd").forEach {
                        Text(it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.caption)
                    }
                }

                // Siatka dni
                val firstDay = selectedDate.withDayOfMonth(1)
                val offset = (firstDay.dayOfWeek.value - 1)
                val daysInMonth = selectedDate.lengthOfMonth()

                val cells = offset + daysInMonth
                val rows = (cells + 6) / 7

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val dayIndex = row * 7 + col - offset + 1
                            if (dayIndex in 1..daysInMonth) {
                                val date = selectedDate.withDayOfMonth(dayIndex)
                                val isSelected = date == selectedDate
                                TextButton(
                                    onClick = { selectedDate = date },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.textButtonColors(
                                        backgroundColor = if (isSelected) AppColors.Primary else androidx.compose.ui.graphics.Color.Transparent,
                                        contentColor = if (isSelected) AppColors.OnPrimary else AppColors.OnBackground
                                    )
                                ) {
                                    Text("$dayIndex", style = MaterialTheme.typography.caption)
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDateSelected(selectedDate) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Anuluj") }
        }
    )
}