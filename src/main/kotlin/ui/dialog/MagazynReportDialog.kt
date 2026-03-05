package ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import report.ExportType
import report.magazyn.MagazynRaportFilter
import ui.AppColors

/** Dialog wyboru filtrów i formatu raportu magazynu */
@Composable
fun MagazynReportDialog(
    availableKontrahenci: List<String>,
    availableSklady: List<String>,
    availableSzerokosci: List<String>,
    onDismiss: () -> Unit,
    onGenerate: (ExportType, MagazynRaportFilter) -> Unit
) {
    var filterKontrahent by remember { mutableStateOf(false) }
    var filterSklad by remember { mutableStateOf(false) }
    var filterSzerokosc by remember { mutableStateOf(false) }

    var selectedKontrahent by remember { mutableStateOf<String?>(null) }
    var selectedSklad by remember { mutableStateOf<String?>(null) }
    var selectedSzerokosc by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generuj raport magazynu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Filtry (opcjonalne):", style = MaterialTheme.typography.subtitle2)

                FilterRow(
                    label = "Kontrahent",
                    enabled = filterKontrahent,
                    onToggle = { filterKontrahent = it; if (!it) selectedKontrahent = null },
                    options = availableKontrahenci,
                    selected = selectedKontrahent,
                    onSelect = { selectedKontrahent = it }
                )

                FilterRow(
                    label = "Skład",
                    enabled = filterSklad,
                    onToggle = { filterSklad = it; if (!it) selectedSklad = null },
                    options = availableSklady,
                    selected = selectedSklad,
                    onSelect = { selectedSklad = it }
                )

                FilterRow(
                    label = "Szerokość",
                    enabled = filterSzerokosc,
                    onToggle = { filterSzerokosc = it; if (!it) selectedSzerokosc = null },
                    options = availableSzerokosci,
                    selected = selectedSzerokosc,
                    onSelect = { selectedSzerokosc = it }
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
                    onClick = {
                        val filter = MagazynRaportFilter(
                            kontrahent = if (filterKontrahent) selectedKontrahent else null,
                            sklad = if (filterSklad) selectedSklad else null,
                            szerokosc = if (filterSzerokosc) selectedSzerokosc else null
                        )
                        onGenerate(ExportType.EXCEL, filter)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Surface)
                ) { Text("Excel") }
                Button(
                    onClick = {
                        val filter = MagazynRaportFilter(
                            kontrahent = if (filterKontrahent) selectedKontrahent else null,
                            sklad = if (filterSklad) selectedSklad else null,
                            szerokosc = if (filterSzerokosc) selectedSzerokosc else null
                        )
                        onGenerate(ExportType.PDF, filter)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Primary)
                ) { Text("PDF", color = AppColors.OnPrimary) }
            }
        }
    )
}

/** Wiersz z checkboxem i dropdownem dla jednego filtra */
@Composable
private fun FilterRow(
    label: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = enabled, onCheckedChange = onToggle)
        Text(label, modifier = Modifier.width(90.dp))
        if (enabled) {
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selected ?: "Wybierz...")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(onClick = { onSelect(option); expanded = false }) {
                            Text(option)
                        }
                    }
                }
            }
        }
    }
}