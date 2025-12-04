package ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.AppColors
import ui.DateRange
import ui.FilterState
import ui.dropdown.DatePickerField
import ui.dropdown.DateRangeDropdown
import ui.dropdown.KontrahentDropdown
import ui.dropdown.MultiStatusDropdown
import ui.dropdown.OddzialDropdown

@Composable
fun FilterPanel(
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    coroutineScope: CoroutineScope,
    onRefresh: () -> Unit,
    onExportExcel: () -> Unit,
    onExportPdf: () -> Unit,
    availableKontrahenci: List<String>
) {
    var expanded by remember { mutableStateOf(true) }
    var showDialogState by remember { mutableStateOf<String?>(null) }

    // Definicja funkcji, która będzie wywołana po zakończeniu eksportu
    val onExportComplete: (Boolean, String) -> Unit = { success, path ->
        // Uruchomienie z powrotem w wątku Compose/Swing
        coroutineScope.launch {
            if (success) {
                showDialogState = "Sukces! Raport zapisano w:\n$path"
            } else {
                showDialogState = "Błąd! Nie udało się zapisać raportu."
            }
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column {
            // Header z przyciskiem zwiń/rozwiń
            Row(
                modifier = Modifier.fillMaxWidth().background(AppColors.Surface).padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = null, tint = AppColors.Primary)
                    Spacer(Modifier.width(4.dp))
                    Text("Filtry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Row {
                    // --- 💾 SEKCJA Raporty ---
                    var showExportMenu by remember { mutableStateOf(false) }

                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(Icons.Default.Save, contentDescription = "Eksportuj raport", tint = AppColors.Primary)
                        }

                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                showExportMenu = false
                                // Zamiast wywoływać serwis, wywołujemy callback
                                onExportExcel()
                            }) {
                                Text("Eksportuj do Excel (.xlsx)")
                            }

                            DropdownMenuItem(onClick = {
                                showExportMenu = false
                                // Zamiast wywoływać serwis, wywołujemy callback
                                onExportPdf()
                            }) {
                                Text("Eksportuj do PDF")
                            }
                        }
                    }

                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Odśwież", tint = AppColors.Primary)
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Zwiń" else "Rozwiń"
                        )
                    }
                }
            }

            if (expanded) {

                // Wyszukiwanie
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = { onFilterChange(filterState.copy(searchQuery = it)) },
                    label = { Text("Szukaj (numer, KIW, receptura)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = AppColors.Surface),
                    singleLine = true
                )

                DateRangeDropdown(
                    selectedRange = filterState.dateRange,
                    onRangeSelected = { onFilterChange(filterState.copy(dateRange = it)) },
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                )

                // Jeśli wybrano CUSTOM, pokaż pola dat
                if (filterState.dateRange == DateRange.CUSTOM) {
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DatePickerField(
                            label = "Data od",
                            date = filterState.customDateFrom,
                            onDateChange = {
                                onFilterChange(filterState.copy(customDateFrom = it))
                            },
                            modifier = Modifier.weight(1f)
                        )

                        DatePickerField(
                            label = "Data do",
                            date = filterState.customDateTo,
                            onDateChange = {
                                onFilterChange(filterState.copy(customDateTo = it))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Oddział, kontahent i Status ZO
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    OddzialDropdown(
                        selectedOddzial = filterState.oddzial,
                        onOddzialSelected = { onFilterChange(filterState.copy(oddzial = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    KontrahentDropdown(
                        availableKontrahenci = availableKontrahenci,
                        selectedKontrahenci = filterState.selectedKontrahenci,
                        onKontrahenciChange = { onFilterChange(filterState.copy(selectedKontrahenci = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    MultiStatusDropdown(
                        label = "Status Zlecenia",
                        selectedStatuses = filterState.selectedStatusZO,
                        onStatusesChange = { onFilterChange(filterState.copy(selectedStatusZO = it)) },
                        modifier = Modifier.weight(1f)
                    )


                }

                Spacer(Modifier.height(2.dp))

                // Statusy ZD, ZL, ZK
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MultiStatusDropdown(
                        label = "Status Drukarnia",
                        selectedStatuses = filterState.selectedStatusZD,
                        onStatusesChange = { onFilterChange(filterState.copy(selectedStatusZD = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    MultiStatusDropdown(
                        label = "Status Laminacja",
                        selectedStatuses = filterState.selectedStatusZL,
                        onStatusesChange = { onFilterChange(filterState.copy(selectedStatusZL = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    MultiStatusDropdown(
                        label = "Status Krajarki",
                        selectedStatuses = filterState.selectedStatusZK,
                        onStatusesChange = { onFilterChange(filterState.copy(selectedStatusZK = it)) },
                        modifier = Modifier.weight(1f)
                    )


                }

                Spacer(Modifier.height(6.dp))

                // Przycisk czyszczenia filtrów
                if (filterState != FilterState()) {
                    Spacer(Modifier.height(12.dp))
                    TextButton(
                        onClick = { onFilterChange(FilterState()) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Wyczyść filtry")
                    }
                }
            }
        }
    }
    showDialogState?.let { message ->
        AlertDialog(
            onDismissRequest = { showDialogState = null },
            title = {
                Text(
                    text = if (message.startsWith("Sukces")) "Eksport zakończony pomyślnie 🎉" else "Wystąpił błąd ❌",
                    color = if (message.startsWith("Sukces")) AppColors.Primary else AppColors.Error
                )
            },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = { showDialogState = null }) {
                    Text("OK")
                }
            }
        )
    }
}