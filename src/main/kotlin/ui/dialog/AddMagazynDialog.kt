package ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import ui.AppColors

import base.ZOPodpowiedzDTO
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun AddMagazynDialog(
    availableZO: List<ZOPodpowiedzDTO>,
    onDismiss: () -> Unit,
    onConfirm: (Int, String?, String?, String?, String?, LocalDateTime?) -> Unit
) {
    var numerInput by remember { mutableStateOf("") }
    var skladMag by remember { mutableStateOf("") }
    var szerokoscMag by remember { mutableStateOf("") }
    var iloscMag by remember { mutableStateOf("") }
    var uwagiMag by remember { mutableStateOf("") }
    var dataProdukcji by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val suggestions = remember(numerInput) {
        if (numerInput.length >= 2) {
            availableZO.filter {
                it.numer.toString().contains(numerInput) ||
                        it.kontrahentNazwa.lowercase().contains(numerInput.lowercase())
            }.take(8)
        } else emptyList()
    }

    val selectedZO = availableZO.firstOrNull { it.numer.toString() == numerInput }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj próbkę do magazynu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Autocomplete numer
                Box {
                    OutlinedTextField(
                        value = numerInput,
                        onValueChange = { numerInput = it; showSuggestions = true },
                        label = { Text("Numer ZO") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    DropdownMenu(
                        expanded = showSuggestions && suggestions.isNotEmpty(),
                        onDismissRequest = { showSuggestions = false }
                    ) {
                        suggestions.forEach { zo ->
                            DropdownMenuItem(onClick = {
                                numerInput = zo.numer.toString()
                                if (skladMag.isBlank()) skladMag = zo.art ?: ""
                                showSuggestions = false
                            }) {
                                Column {
                                    Text("${zo.numer} — ${zo.kontrahentNazwa}", fontWeight = FontWeight.Bold)
                                    Text("${zo.art ?: "-"} / ${zo.receptura ?: "-"}",
                                        style = MaterialTheme.typography.caption)
                                }
                            }
                        }
                    }
                }

                // Info z systemu (readonly jeśli wybrany)
                if (selectedZO != null) {
                    Text("Kontrahent: ${selectedZO.kontrahentNazwa} | Art: ${selectedZO.art ?: "-"} | Receptura: ${selectedZO.receptura ?: "-"}",
                        style = MaterialTheme.typography.caption)
                }

                OutlinedTextField(value = skladMag, onValueChange = { skladMag = it },
                    label = { Text("Skład") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = szerokoscMag, onValueChange = { szerokoscMag = it },
                    label = { Text("Szerokość") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = iloscMag, onValueChange = { iloscMag = it },
                    label = { Text("Ilość") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = uwagiMag, onValueChange = { uwagiMag = it },
                    label = { Text("Uwagi") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dataProdukcji, onValueChange = { dataProdukcji = it },
                    label = { Text("Data produkcji (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val numer = numerInput.toIntOrNull() ?: return@Button
                    val data = try {
                        if (dataProdukcji.isBlank()) null
                        else LocalDate.parse(dataProdukcji).atStartOfDay()
                    } catch (e: Exception) { null }

                    onConfirm(numer, skladMag.ifBlank { null }, szerokoscMag.ifBlank { null },
                        iloscMag.ifBlank { null }, uwagiMag.ifBlank { null }, data)
                },
                enabled = numerInput.toIntOrNull() != null
            ) { Text("Dodaj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}