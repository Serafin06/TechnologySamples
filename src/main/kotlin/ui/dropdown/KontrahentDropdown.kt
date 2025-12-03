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
fun KontrahentDropdown(
    availableKontrahenci: List<String>,
    selectedKontrahenci: Set<String>,
    onKontrahenciChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredKontrahenci = remember(availableKontrahenci, searchQuery) {
        if (searchQuery.isBlank()) {
            availableKontrahenci.sorted()
        } else {
            availableKontrahenci.filter {
                it.contains(searchQuery, ignoreCase = true)
            }.sorted()
        }
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = when {
                selectedKontrahenci.isEmpty() -> "Wszyscy kontrahenci"
                selectedKontrahenci.size == 1 -> selectedKontrahenci.first()
                else -> "${selectedKontrahenci.size} wybranych"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("Kontrahent") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = AppColors.Surface
            ),
            trailingIcon = {
                Row {
                    if (selectedKontrahenci.isNotEmpty()) {
                        IconButton(
                            onClick = { onKontrahenciChange(emptySet()) },
                            modifier = Modifier.Companion.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Wyczyść",
                                modifier = Modifier.Companion.size(16.dp)
                            )
                        }
                    }
                    IconButton(onClick = {
                        expanded = !expanded
                        if (!expanded) searchQuery = ""
                    }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
            },
            modifier = Modifier.Companion.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            },
            modifier = Modifier.Companion.heightIn(max = 400.dp).width(300.dp)
        ) {
            // Pole wyszukiwania w menu
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Szukaj...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.Companion.fillMaxWidth().padding(8.dp),
                singleLine = true
            )

            Divider()

            filteredKontrahenci.forEach { kontrahent ->
                DropdownMenuItem(
                    onClick = {
                        val newSet = if (selectedKontrahenci.contains(kontrahent)) {
                            selectedKontrahenci - kontrahent
                        } else {
                            selectedKontrahenci + kontrahent
                        }
                        onKontrahenciChange(newSet)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.Companion.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selectedKontrahenci.contains(kontrahent),
                            onCheckedChange = null
                        )
                        Text(kontrahent)
                    }
                }
            }
        }
    }
}