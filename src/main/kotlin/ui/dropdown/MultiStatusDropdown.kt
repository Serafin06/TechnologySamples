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
import ui.StatusBadge
import ui.heightCell

@Composable
fun MultiStatusDropdown(
    label: String,
    selectedStatuses: Set<Byte>,
    onStatusesChange: (Set<Byte>) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    var expanded by remember { mutableStateOf(false) }

    // Mapa, która przypisuje kod statusu do jego nazwy
    val statusy = mapOf(
        0.toByte() to "Wykonane",
        1.toByte() to "Do realizacji",
        2.toByte() to "Zaplanowane",
        3.toByte() to "Wstrzymane",
        4.toByte() to "Anulowane"
    )

    Box(modifier = modifier) {
        OutlinedTextField(
            value = when {
                selectedStatuses.isEmpty() -> "Wszystkie"
                selectedStatuses.size == 1 -> statusy[selectedStatuses.first()] ?: ""
                else -> "${selectedStatuses.size} wybrane"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = AppColors.Surface
            ),
            trailingIcon = {
                Row {
                    if (selectedStatuses.isNotEmpty()) {
                        IconButton(
                            onClick = { onStatusesChange(emptySet()) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Wyczyść",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
            },
            modifier = Modifier.height(heightCell).fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statusy.forEach { (code, name) ->
                DropdownMenuItem(
                    onClick = {
                        val newSet = if (selectedStatuses.contains(code)) {
                            selectedStatuses - code
                        } else {
                            selectedStatuses + code
                        }
                        onStatusesChange(newSet)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selectedStatuses.contains(code),
                            onCheckedChange = null
                        )
                        StatusBadge(code, compact = true)
                        Spacer(Modifier.width(4.dp))
                        Text(name)
                    }
                }
            }
        }
    }
}