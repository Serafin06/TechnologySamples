package ui.dropdown

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.AppColors

@Composable
fun FlagDropdown(
    label: String,
    selectedFlags: Set<Boolean?>,
    onFlagsChange: (Set<Boolean?>) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Logika wyświetlania tekstu w polu, spójna z MultiStatusDropdown
    val displayText = when {
        selectedFlags.isEmpty() -> "Wszystkie"
        selectedFlags.size == 1 -> when (selectedFlags.first()) {
            true -> "Tak"
            false -> "W realizacji"
            null -> "Brak danych"
        }
        else -> "${selectedFlags.size} wybrane"
    }

    Box(modifier = modifier) {
        // ZMIANA 1: Użycie OutlinedTextField zamiast OutlinedButton
        OutlinedTextField(
            value = displayText,
            onValueChange = {}, // Pole jest tylko do odczytu
            readOnly = true,
            label = { Text(label) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = AppColors.Surface
            ),
            // ZMIANA 2: Przeniesienie ikon do trailingIcon
            trailingIcon = {
                Row {
                    // Przycisk czyszczenia, widoczny tylko gdy są zaznaczone opcje
                    if (selectedFlags.isNotEmpty()) {
                        IconButton(
                            onClick = { onFlagsChange(emptySet()) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Wyczyść",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    // Przycisk rozwijania/zamykania menu
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            // ZMIANA 3: Użycie ikon ExpandMore/ExpandLess
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Opcje flag pozostają bez zmian
            FlagOption("Tak", true, selectedFlags, onFlagsChange)
            FlagOption("W realizacji", false, selectedFlags, onFlagsChange)
            FlagOption("Brak danych", null, selectedFlags, onFlagsChange)

            // ZMIANA 4: Usunięcie przycisku "Wyczyść" i Dividera z menu,
            // ponieważ funkcja ta jest teraz realizowana przez IconButton w trailingIcon.
        }
    }
}

// Funkcja pomocnicza pozostaje bez zmian, jest dobrze napisana
@Composable
private fun FlagOption(
    text: String,
    value: Boolean?,
    selectedFlags: Set<Boolean?>,
    onFlagsChange: (Set<Boolean?>) -> Unit
) {
    DropdownMenuItem(onClick = {
        val newSet = if (selectedFlags.contains(value)) {
            selectedFlags - value
        } else {
            selectedFlags + value
        }
        onFlagsChange(newSet)
    }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = selectedFlags.contains(value),
                onCheckedChange = null
            )
            Text(text, style = MaterialTheme.typography.body2)
        }
    }
}