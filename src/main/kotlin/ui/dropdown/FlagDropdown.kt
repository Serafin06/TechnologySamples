package ui.dropdown

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
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

    val displayText = when {
        selectedFlags.isEmpty() -> "Wszystkie"
        selectedFlags.size == 3 -> "Wszystkie"
        else -> {
            selectedFlags.joinToString(", ") {
                when(it) {
                    true -> "Tak"
                    false -> "Nie"
                    null -> "Anulowane"
                }
            }
        }
    }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = AppColors.Surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.caption)
                    Text(displayText, style = MaterialTheme.typography.body2)
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FlagOption("Tak", true, selectedFlags, onFlagsChange)
            FlagOption("W realizacji", false, selectedFlags, onFlagsChange)
            FlagOption("Brak danych", null, selectedFlags, onFlagsChange)

            Divider()

            DropdownMenuItem(onClick = {
                onFlagsChange(emptySet())
                expanded = false
            }) {
                Text("Wyczyść", style = MaterialTheme.typography.body2)
            }
        }
    }
}

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
        Checkbox(
            checked = selectedFlags.contains(value),
            onCheckedChange = null
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.body2)
    }
}