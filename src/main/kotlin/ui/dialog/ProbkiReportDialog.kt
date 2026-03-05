package ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import report.ExportType
import report.RaportFilter
import ui.AppColors

@Composable
fun ProbkiReportDialog(
    availableKontrahenci: List<String>,
    onDismiss: () -> Unit,
    onGenerate: (ExportType, RaportFilter) -> Unit
) {
    var filterZaklad by remember { mutableStateOf(false) }
    var filterKontrahent by remember { mutableStateOf(false) }
    var filterProduce by remember { mutableStateOf(false) }
    var filterSend by remember { mutableStateOf(false) }
    var filterTested by remember { mutableStateOf(false) }
    var tylkoOtwarte by remember { mutableStateOf(false) }

    var selectedZaklad by remember { mutableStateOf<String?>(null) }
    var selectedKontrahent by remember { mutableStateOf<String?>(null) }
    var selectedProduce by remember { mutableStateOf<Boolean?>(null) }
    var selectedSend by remember { mutableStateOf<Boolean?>(null) }
    var selectedTested by remember { mutableStateOf<Boolean?>(null) }

    fun buildFilter() = RaportFilter(
        oddzialNazwa = if (filterZaklad) selectedZaklad else null,
        tylkoOtwarte = tylkoOtwarte,
        kontrahent = if (filterKontrahent) selectedKontrahent else null,
        produce = if (filterProduce) selectedProduce else null,
        send = if (filterSend) selectedSend else null,
        tested = if (filterTested) selectedTested else null
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generuj raport próbek") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tylko otwarte
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = tylkoOtwarte, onCheckedChange = { tylkoOtwarte = it })
                    Text("Tylko otwarte (W realizacji / Planowane)")
                }

                Divider()
                Text("Filtry:", style = MaterialTheme.typography.subtitle2)

                // Zakład
                FilterRow(
                    label = "Zakład",
                    enabled = filterZaklad,
                    onToggle = { filterZaklad = it; if (!it) selectedZaklad = null },
                    options = listOf("Tychy", "Ignatki"),
                    selected = selectedZaklad,
                    onSelect = { selectedZaklad = it }
                )

                // Kontrahent
                FilterRow(
                    label = "Kontrahent",
                    enabled = filterKontrahent,
                    onToggle = { filterKontrahent = it; if (!it) selectedKontrahent = null },
                    options = availableKontrahenci,
                    selected = selectedKontrahent,
                    onSelect = { selectedKontrahent = it }
                )

                // Wyprodukowane
                BoolFilterRow(
                    label = "Wyprodukowane",
                    enabled = filterProduce,
                    onToggle = { filterProduce = it; if (!it) selectedProduce = null },
                    selected = selectedProduce,
                    onSelect = { selectedProduce = it }
                )

                // Wysłane
                BoolFilterRow(
                    label = "Wysłane",
                    enabled = filterSend,
                    onToggle = { filterSend = it; if (!it) selectedSend = null },
                    selected = selectedSend,
                    onSelect = { selectedSend = it }
                )

                // Testy
                BoolFilterRow(
                    label = "Testy",
                    enabled = filterTested,
                    onToggle = { filterTested = it; if (!it) selectedTested = null },
                    selected = selectedTested,
                    onSelect = { selectedTested = it }
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = onDismiss) { Text("Anuluj") }
                Button(
                    onClick = { onGenerate(ExportType.EXCEL, buildFilter()) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Surface)
                ) { Text("Excel") }
                Button(
                    onClick = { onGenerate(ExportType.PDF, buildFilter()) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) { Text("PDF", color = AppColors.OnPrimary) }
            }
        }
    )
}

/** Dropdown z wartościami Tak/Nie */
@Composable
private fun BoolFilterRow(
    label: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    selected: Boolean?,
    onSelect: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = enabled, onCheckedChange = onToggle)
        Text(label, modifier = Modifier.width(120.dp))
        if (enabled) {
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(when (selected) { true -> "Tak"; false -> "Nie"; null -> "Wybierz..." })
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = { onSelect(true); expanded = false }) { Text("Tak") }
                    DropdownMenuItem(onClick = { onSelect(false); expanded = false }) { Text("Nie") }
                }
            }
        }
    }
}