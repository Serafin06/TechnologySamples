package pl.rafapp.techSam.UI

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

                // Filtry w rzędach
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Oddział
                    OddzialDropdown(
                        selectedOddzial = filterState.oddzial,
                        onOddzialSelected = { onFilterChange(filterState.copy(oddzial = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    // Status ZO
                    StatusDropdown(
                        label = "Status ZO",
                        selectedStatus = filterState.stanZO,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZO = it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Status ZK
                    StatusDropdown(
                        label = "Status ZK",
                        selectedStatus = filterState.stanZK,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZK = it)) },
                        modifier = Modifier.weight(1f)
                    )

                    // Status ZD
                    StatusDropdown(
                        label = "Status ZD",
                        selectedStatus = filterState.stanZD,
                        onStatusSelected = { onFilterChange(filterState.copy(stanZD = it)) },
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
