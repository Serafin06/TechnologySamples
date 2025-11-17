package pl.rafapp.techSam.UI

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 🏢 Dropdown Components

@Composable
fun OddzialDropdown(
    selectedOddzial: String?,
    onOddzialSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val oddzialy = listOf("Ignatki", "Tychy")

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedOddzial ?: "Wszystkie",
            onValueChange = {},
            readOnly = true,
            label = { Text("Zakład") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                onOddzialSelected(null)
                expanded = false
            }) {
                Text("Wszystkie")
            }
            oddzialy.forEach { oddzial ->
                DropdownMenuItem(onClick = {
                    onOddzialSelected(oddzial)
                    expanded = false
                }) {
                    Text(oddzial)
                }
            }
        }
    }
}

@Composable
fun StatusDropdown(
    label: String,
    selectedStatus: Byte?,
    onStatusSelected: (Byte?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val statusy = mapOf(
        0.toByte() to "Wykonane",
        1.toByte() to "Do realizacji",
        2.toByte() to "Zaplanowane",
        3.toByte() to "Wstrzymane",
        4.toByte() to "Anulowane"
    )

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedStatus?.let { statusy[it] } ?: "Wszystkie",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                onStatusSelected(null)
                expanded = false
            }) {
                Text("Wszystkie")
            }
            statusy.forEach { (stan, nazwa) ->
                DropdownMenuItem(onClick = {
                    onStatusSelected(stan)
                    expanded = false
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(stan, compact = true)
                        Spacer(Modifier.width(8.dp))
                        Text(nazwa)
                    }
                }
            }
        }
    }
}