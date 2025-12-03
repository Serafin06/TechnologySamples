package ui.dropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.AppColors
import ui.DateRange
import java.time.LocalDateTime

@Composable
fun OddzialDropdown(
    selectedOddzial: String?,
    onOddzialSelected: (String?) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    var expanded by remember { mutableStateOf(false) }
    val oddzialy = listOf("Ignatki", "Tychy")

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedOddzial ?: "Wszystkie",
            onValueChange = {},
            readOnly = true,
            label = { Text("Zakład") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = AppColors.Surface,
                textColor = Color.Companion.Black
            ),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.Companion.fillMaxWidth()
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
fun DateRangeDropdown(
    selectedRange: DateRange,
    onRangeSelected: (DateRange) -> Unit,
    modifier: Modifier = Modifier.Companion
    ) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedRange.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Zakres danych") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = AppColors.Surface,
                disabledLabelColor = Color.Companion.Black
            ),
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.Companion.clickable { expanded = true }
                )
            },
            modifier = Modifier.Companion.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DateRange.values().forEach { range ->
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
    modifier: Modifier = Modifier.Companion
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date?.toString()?.take(10) ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = AppColors.Surface,
            textColor = Color.Companion.Black
        ),
        trailingIcon = {
            Row {
                if (date != null) {
                    IconButton(
                        onClick = { onDateChange(null) },
                        modifier = Modifier.Companion.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Wyczyść",
                            modifier = Modifier.Companion.size(16.dp)
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
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = year.toString(),
                        onValueChange = { year = it.toIntOrNull() ?: year },
                        label = { Text("Rok") },
                        modifier = Modifier.Companion.weight(1f)
                    )
                    OutlinedTextField(
                        value = month.toString(),
                        onValueChange = { month = (it.toIntOrNull() ?: month).coerceIn(1, 12) },
                        label = { Text("Miesiąc") },
                        modifier = Modifier.Companion.weight(1f)
                    )
                    OutlinedTextField(
                        value = day.toString(),
                        onValueChange = { day = (it.toIntOrNull() ?: day).coerceIn(1, 31) },
                        label = { Text("Dzień") },
                        modifier = Modifier.Companion.weight(1f)
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