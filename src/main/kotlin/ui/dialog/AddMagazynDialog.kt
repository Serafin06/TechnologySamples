package ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.AppColors
import base.ProbkaDTO
import java.time.LocalDate
import java.time.LocalDateTime

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
                val type = when (layers.size) {
                    1 -> "Taśma"; 2 -> "Laminat"; else -> "Trilaminat"
                }
                strukturaMag = "$type ${layers.joinToString("/")}"

                // Możesz tu dodać inne pola, jeśli chcesz je kopiować, np. ilość z zamówienia
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
                    OutlinedTextField(
                        value = dataProdukcji,
                        onValueChange = { dataProdukcji = it },
                        label = { Text("Data produkcji (yyyy-MM-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
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