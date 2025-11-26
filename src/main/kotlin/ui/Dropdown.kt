package pl.rafapp.techSam.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.rafapp.techSam.ui.AppColors
import pl.rafapp.techSam.ui.DateRange
import java.time.LocalDateTime

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
            colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = AppColors.Surface, textColor = Color.Black),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = AppColors.Surface, textColor = Color.Black),
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

@Composable
fun DateRangeDropdown(
    selectedRange: DateRange,
    onRangeSelected: (pl.rafapp.techSam.ui.DateRange) -> Unit,
    modifier: Modifier = Modifier
    ) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedRange.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Zakres danych") },
            colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = _root_ide_package_.pl.rafapp.techSam.ui.AppColors.Surface, disabledLabelColor = Color.Black),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            _root_ide_package_.pl.rafapp.techSam.ui.DateRange.values().forEach { range ->
                DropdownMenuItem(
                    onClick = {
                        onRangeSelected(range)
                        expanded = false
                    }
                ) {
                    Text(range.label)
                }
            }
        }
    }
}

// dzialanie Picera Dat, 3 domysne wartosci do wyboru plus mozliwosc wyboru przez uzytkownika dat
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
        colors = TextFieldDefaults.outlinedTextFieldColors(backgroundColor = _root_ide_package_.pl.rafapp.techSam.ui.AppColors.Surface, textColor = Color.Black),
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
