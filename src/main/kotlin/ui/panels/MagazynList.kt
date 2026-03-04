package ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.MagazynDTO
import ui.AppColors
import ui.heightCell
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class MagazynSortColumn { NUMER, KONTRAHENT, SKLAD, STRUKTURA, SZEROKOSC, ILOSC, DATA }
data class SortState(val column: MagazynSortColumn, val ascending: Boolean = true)

@Composable
fun MagazynList(
    probki: List<MagazynDTO>,
    isEditMode: Boolean,
    onSave: (Int, String?, String?, String?, String?, String?, LocalDateTime?) -> Unit,
    onDelete: (Int) -> Unit
) {
    var sortState by remember { mutableStateOf<SortState?>(null) }

    val sortedProbki = remember(probki, sortState) {
        val s = sortState ?: return@remember probki
        val sorted = when (s.column) {
            MagazynSortColumn.NUMER -> probki.sortedBy { it.numer }
            MagazynSortColumn.KONTRAHENT -> probki.sortedBy { it.kontrahentNazwa }
            MagazynSortColumn.SKLAD -> probki.sortedBy { it.skladMag ?: "" }
            MagazynSortColumn.STRUKTURA -> probki.sortedBy { it.strukturaMag ?: "" }
            MagazynSortColumn.SZEROKOSC -> probki.sortedBy { it.szerokoscMag?.toDoubleOrNull() ?: Double.MAX_VALUE }
            MagazynSortColumn.ILOSC -> probki.sortedBy { it.iloscMag?.toDoubleOrNull() ?: Double.MAX_VALUE }
            MagazynSortColumn.DATA -> probki.sortedBy { it.dataProdukcjiMag }
        }
        if (s.ascending) sorted else sorted.reversed()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            MagazynTableHeader(
                sortState = sortState,
                onSort = { col ->
                    sortState = if (sortState?.column == col)
                        sortState!!.copy(ascending = !sortState!!.ascending)
                    else SortState(col)
                }
            )
        }

        items(sortedProbki, key = {it.numer}) { probka ->
            MagazynRow(
                probka = probka,
                isEditMode = isEditMode,
                onSave = onSave,
                onDelete = onDelete
            )
        }
    }
}

@Composable
fun MagazynTableHeader(
    sortState: SortState?,
    onSort: (MagazynSortColumn) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightCell)
            .background(AppColors.Primary)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortableHeaderCell("Numer", 0.07f, MagazynSortColumn.NUMER, sortState, onSort)
        SortableHeaderCell("Kontrahent", 0.18f, MagazynSortColumn.KONTRAHENT, sortState, onSort)
        SortableHeaderCell("Skład", 0.15f, MagazynSortColumn.SKLAD, sortState, onSort)
        SortableHeaderCell("Struktura", 0.12f, MagazynSortColumn.STRUKTURA, sortState, onSort)
        SortableHeaderCell("Szerokość", 0.10f, MagazynSortColumn.SZEROKOSC, sortState, onSort)
        SortableHeaderCell("Ilość", 0.10f, MagazynSortColumn.ILOSC, sortState, onSort)
        HeaderCell("Uwagi", 0.16f)
        SortableHeaderCell("Data prod.", 0.12f, MagazynSortColumn.DATA, sortState, onSort)
    }
}

@Composable
fun RowScope.SortableHeaderCell(
    text: String, weight: Float,
    column: MagazynSortColumn,
    sortState: SortState?,
    onSort: (MagazynSortColumn) -> Unit
) {
    val indicator = when {
        sortState?.column != column -> ""
        sortState.ascending -> " ▲"
        else -> " ▼"
    }
    Text(
        text = text + indicator,
        modifier = Modifier.weight(weight).clickable { onSort(column) },
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        color = AppColors.OnPrimary
    )
}

@Composable
fun RowScope.HeaderCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        color = AppColors.OnPrimary
    )
}

@Composable
fun MagazynRow(
    probka: MagazynDTO,
    isEditMode: Boolean,
    onSave: (Int, String?, String?, String?, String?, String?, LocalDateTime?) -> Unit,
    onDelete: (Int) -> Unit
) {
    var skladMag by remember { mutableStateOf(probka.skladMag ?: "") }
    var strukturaMag by remember { mutableStateOf(probka.strukturaMag ?: "") }
    var szerokoscMag by remember { mutableStateOf(probka.szerokoscMag ?: "") }
    var iloscMag by remember { mutableStateOf(probka.iloscMag ?: "") }
    var uwagiMag by remember { mutableStateOf(probka.uwagiMag ?: "") }
    val dataProdukcjiMag by remember { mutableStateOf(probka.dataProdukcjiMag) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            onSave(
                probka.numer,
                strukturaMag.ifBlank { null },
                skladMag.ifBlank { null },
                szerokoscMag.ifBlank { null },
                iloscMag.ifBlank { null },
                uwagiMag.ifBlank { null },
                dataProdukcjiMag
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightCell)
            .border(1.dp, AppColors.Primary.copy(alpha = 0.3f)),
        elevation = 2.dp,
        backgroundColor = AppColors.Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .height(if (isEditMode) 64.dp else heightCell)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = probka.numer.toString(),
                modifier = Modifier.weight(0.06f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground
            )


            Text(
                text = probka.kontrahentNazwa,
                modifier = Modifier.weight(0.13f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground
            )

            if (isEditMode) {
                OutlinedTextField(
                    value = skladMag,
                    onValueChange = { skladMag = it },
                    modifier = Modifier.weight(0.13f).padding(horizontal = 2.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.OnBackground,
                        backgroundColor = AppColors.Surface
                    )
                )
            } else {
                Text(
                    text = skladMag.ifBlank { "-" },
                    modifier = Modifier.weight(0.13f),
                    style = MaterialTheme.typography.body2,
                    color = AppColors.OnBackground
                )
            }

            if (isEditMode) {
                OutlinedTextField(
                    value = strukturaMag,
                    onValueChange = { strukturaMag = it },
                    modifier = Modifier.weight(0.12f).padding(horizontal = 2.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.OnBackground,
                        backgroundColor = AppColors.Surface
                    )
                )
            } else {
                Text(
                    text = strukturaMag.ifBlank { "-" },
                    modifier = Modifier.weight(0.12f),
                    style = MaterialTheme.typography.body2,
                    color = AppColors.OnBackground
                )
            }

            if (isEditMode) {
                OutlinedTextField(
                    value = szerokoscMag,
                    onValueChange = { szerokoscMag = it },
                    modifier = Modifier.weight(0.10f).padding(horizontal = 2.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.OnBackground,
                        backgroundColor = AppColors.Surface
                    )
                )
            } else {
                Text(
                    text = szerokoscMag.ifBlank { "-" },
                    modifier = Modifier.weight(0.10f),
                    style = MaterialTheme.typography.body2,
                    color = AppColors.OnBackground
                )
            }

            if (isEditMode) {
                OutlinedTextField(
                    value = iloscMag,
                    onValueChange = { iloscMag = it },
                    modifier = Modifier.weight(0.10f).padding(horizontal = 2.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.OnBackground,
                        backgroundColor = AppColors.Surface
                    )
                )
            } else {
                Text(
                    text = iloscMag.ifBlank { "-" },
                    modifier = Modifier.weight(0.10f),
                    style = MaterialTheme.typography.body2,
                    color = AppColors.OnBackground
                )
            }

            if (isEditMode) {
                OutlinedTextField(
                    value = uwagiMag,
                    onValueChange = { uwagiMag = it },
                    modifier = Modifier.weight(0.15f).padding(horizontal = 2.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.OnBackground,
                        backgroundColor = AppColors.Surface
                    )
                )
            } else {
                Text(
                    text = uwagiMag.ifBlank { "-" },
                    modifier = Modifier.weight(0.15f),
                    style = MaterialTheme.typography.body2,
                    color = AppColors.OnBackground
                )
            }

            Text(
                text = dataProdukcjiMag?.format(dateFormatter) ?: "-",
                modifier = Modifier.weight(0.10f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground
            )
            if (isEditMode) {
                IconButton(
                    onClick = { onDelete(probka.numer) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Usuń",
                        tint = AppColors.Error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(Modifier.width(32.dp))
            }
        }
    }
}