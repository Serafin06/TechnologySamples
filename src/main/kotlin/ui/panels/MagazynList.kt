package ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
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

@Composable
fun MagazynList(
    probki: List<MagazynDTO>,
    isEditMode: Boolean,
    onSave: (Int, String?, String?, String?, String?, LocalDateTime?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            MagazynTableHeader()
        }

        items(probki) { probka ->
            MagazynRow(
                probka = probka,
                isEditMode = isEditMode,
                onSave = onSave
            )
        }
    }
}

@Composable
fun MagazynTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightCell)
            .background(AppColors.Primary)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell("Numer", 0.06f)
        HeaderCell("Oddział", 0.07f)
        HeaderCell("Kontrahent", 0.13f)
        HeaderCell("Art", 0.08f)
        HeaderCell("Receptura", 0.08f)
        HeaderCell("Skład", 0.13f)
        HeaderCell("Szerokość", 0.10f)
        HeaderCell("Ilość", 0.10f)
        HeaderCell("Uwagi", 0.15f)
        HeaderCell("Data prod.", 0.10f)
    }
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
    onSave: (Int, String?, String?, String?, String?, LocalDateTime?) -> Unit
) {
    var skladMag by remember { mutableStateOf(probka.skladMag ?: "") }
    var szerokoscMag by remember { mutableStateOf(probka.szerokoscMag ?: "") }
    var iloscMag by remember { mutableStateOf(probka.iloscMag ?: "") }
    var uwagiMag by remember { mutableStateOf(probka.uwagiMag ?: "") }
    val dataProdukcjiMag by remember { mutableStateOf(probka.dataProdukcjiMag) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            onSave(
                probka.numer,
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
                text = probka.oddzialNazwa,
                modifier = Modifier.weight(0.07f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground
            )

            Text(
                text = probka.kontrahentNazwa,
                modifier = Modifier.weight(0.13f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground
            )

            Text(
                text = probka.art ?: "-",
                modifier = Modifier.weight(0.08f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground
            )

            Text(
                text = probka.receptura ?: "-",
                modifier = Modifier.weight(0.08f),
                style = MaterialTheme.typography.body2,
                color = AppColors.OnBackground.copy(alpha = 0.7f)
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
        }
    }
}