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
import ui.dropdown.FlagDropdown
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

    val onExportComplete: (Boolean, String) -> Unit = { success, path ->
        coroutineScope.launch {
            if (success) {
                showDialogState = "Sukces! Raport zapisano w:\n$path"
            } else {
                showDialogState = "Błąd! Nie udało się zapisać raportu."
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column {
            // Header - bez zmian, ale mniejsza czcionka
            Row(
                modifier = Modifier.fillMaxWidth().background(AppColors.Surface).padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = null, tint = AppColors.Primary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Filtry",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (expanded) 16.sp else 14.sp // ✅ Mniejsza gdy zwinięte
                    )
                }

                Row {
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
                                onExportExcel()
                            }) {
                                Text("Eksportuj do Excel (.xlsx)")
                            }

                            DropdownMenuItem(onClick = {
                                showExportMenu = false
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
                // ✅ Zmniejszone paddingi: 10dp -> 6dp, height 8dp -> 4dp

                // Wyszukiwanie
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = { onFilterChange(filterState.copy(searchQuery = it)) },
                    label = { Text("Szukaj (numer, KIW, receptura)", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = AppColors.Surface),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                )

                DateRangeDropdown(
                    selectedRange = filterState.dateRange,
                    onRangeSelected = { onFilterChange(filterState.copy(dateRange = it)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                )

                // Custom daty
                if (filterState.dateRange == DateRange.CUSTOM) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DatePickerField(
                            label = "Data od",
                            date = filterState.customDateFrom,
                            onDateChange = { onFilterChange(filterState.copy(customDateFrom = it)) },
                            modifier = Modifier.weight(1f)
                        )

                        DatePickerField(
                            label = "Data do",
                            date = filterState.customDateTo,
                            onDateChange = { onFilterChange(filterState.copy(customDateTo = it)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Oddział, Kontrahent, Status ZO
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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

                // Statusy ZD, ZL, ZK
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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

                // ✅ NOWY WIERSZ: Flagi technologii
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FlagDropdown(
                        label = "Wyprodukowana",
                        selectedFlags = filterState.selectedProduce,
                        onFlagsChange = { onFilterChange(filterState.copy(selectedProduce = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    FlagDropdown(
                        label = "Wysłana",
                        selectedFlags = filterState.selectedSend,
                        onFlagsChange = { onFilterChange(filterState.copy(selectedSend = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    FlagDropdown(
                        label = "Testy",
                        selectedFlags = filterState.selectedTested,
                        onFlagsChange = { onFilterChange(filterState.copy(selectedTested = it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Przycisk czyszczenia - porównaj z domyślnym stanem
                // 1. Stwórz obiekt stanu "czystego" do porównań
                val clearedState = FilterState.cleared()

                // 2. Sprawdź, czy aktualny stan różni się od stanu "czystego"
                //    To jest bardzo prosta i czytelna logika!
                val isAnyFilterActive = filterState != clearedState

                // 3. Wyświetl przycisk, jeśli jakikolwiek filtr jest aktywny
                if (isAnyFilterActive) {
                    TextButton(
                        // 4. Przy kliknięciu ustaw stan na "czysty"
                        onClick = { onFilterChange(clearedState) },
                        modifier = Modifier.align(Alignment.End).padding(4.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Wyczyść filtry", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Dialog bez zmian
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