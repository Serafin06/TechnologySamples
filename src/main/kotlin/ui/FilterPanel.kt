package ui

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
import base.ProbkaService
import base.StatusResolver
import kotlinx.coroutines.CoroutineScope
import report.ExportType
import report.generujRaportAkcja
import ui.AppColors
import ui.DateRange
import ui.FilterState


// 🔍 Filter Panel

@Composable
fun FilterPanel(
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    coroutineScope: CoroutineScope,
    probkaService: ProbkaService,
    onRefresh: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

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
                                generujRaportAkcja(coroutineScope, ExportType.EXCEL, probkaService)
                            }) {
                                Text("Eksportuj do Excel (.xlsx)")
                            }

                            DropdownMenuItem(onClick = {
                                showExportMenu = false
                                generujRaportAkcja(coroutineScope, ExportType.PDF, probkaService)
                            }) {
                                Text("Eksportuj do PDF (Wkrótce)")
                            }
                        }
                    }
                }

                Row {
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
                    label = { Text("Szukaj (numer, ART, receptura)") },
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

                // Oddział i Status ZO
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    OddzialDropdown(
                        selectedOddzial = filterState.oddzial,
                        onOddzialSelected = { onFilterChange(filterState.copy(oddzial = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    StatusDropdown(
                        label = "Status ZO",
                        selectedStatus = filterState.stanZO,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZO = it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(2.dp))

                // Statusy ZD, ZL, ZK
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusDropdown(
                        label = "Status ZK",
                        selectedStatus = filterState.stanZK,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZK = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    StatusDropdown(
                        label = "Status ZD",
                        selectedStatus = filterState.stanZD,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZD = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    StatusDropdown(
                        label = "Status ZL",
                        selectedStatus = filterState.stanZL,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZL = it)) },
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
}

