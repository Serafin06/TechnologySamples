package pl.rafapp.techSam.UI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime

// 🔍 Filter Panel

@Composable
fun FilterPanel(
    filterState: FilterState,
    onFilterChange: (FilterState) -> Unit,
    onRefresh: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header z przyciskiem zwiń/rozwiń
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = AppColors.Primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Filtry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                Spacer(Modifier.height(16.dp))

                // Wyszukiwanie
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = { onFilterChange(filterState.copy(searchQuery = it)) },
                    label = { Text("Szukaj (numer, ART, receptura)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                DateRangeDropdown(
                    selectedRange = filterState.dateRange,
                    onRangeSelected = { onFilterChange(filterState.copy(dateRange = it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Jeśli wybrano CUSTOM, pokaż pola dat
                if (filterState.dateRange == DateRange.CUSTOM) {
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                Spacer(Modifier.height(12.dp))

                Spacer(Modifier.height(12.dp))

                // Oddział i Status ZO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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

                Spacer(Modifier.height(12.dp))

                // Statusy ZD, ZL, ZK
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

@Composable
fun DatePickerField(
    label: String,
    date: LocalDateTime?,
    onDateChange: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date?.toString()?.take(10) ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Row {
                if (date != null) {
                    IconButton(
                        onClick = { onDateChange(null) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Wyczyść",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Wybierz datę")
                }
            }
        },
        modifier = modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        SimpleDatePickerDialog(
            initialDate = date ?: LocalDateTime.now(),
            onDateSelected = {
                onDateChange(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

// Prosty dialog wyboru daty
@Composable
fun SimpleDatePickerDialog(
    initialDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableStateOf(initialDate.year) }
    var month by remember { mutableStateOf(initialDate.monthValue) }
    var day by remember { mutableStateOf(initialDate.dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz datę") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = year.toString(),
                        onValueChange = { year = it.toIntOrNull() ?: year },
                        label = { Text("Rok") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = month.toString(),
                        onValueChange = { month = (it.toIntOrNull() ?: month).coerceIn(1, 12) },
                        label = { Text("Miesiąc") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = day.toString(),
                        onValueChange = { day = (it.toIntOrNull() ?: day).coerceIn(1, 31) },
                        label = { Text("Dzień") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    onDateSelected(LocalDateTime.of(year, month, day, 0, 0))
                } catch (e: Exception) {
                    // Nieprawidłowa data
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
