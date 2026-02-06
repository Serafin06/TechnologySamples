package ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ui.AppColors
import ui.MagazynViewModel

@Composable
fun MagazynScreen(viewModel: MagazynViewModel) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadMagazynProbki()
    }

    MaterialTheme(
        colors = lightColors(
            primary = AppColors.Primary,
            secondary = AppColors.Secondary,
            background = AppColors.Background,
            surface = AppColors.Surface,
            error = AppColors.Error
        )
    ) {
        Scaffold(
            topBar = {
                MagazynTopBar(
                    isEditMode = viewModel.isEditMode,
                    onToggleEditMode = { viewModel.toggleEditMode() },
                    searchQuery = viewModel.searchQuery.collectAsState().value,
                    onSearchChange = { viewModel.updateSearchQuery(it) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background)
            ) {
                when {
                    viewModel.isLoading -> MagazynLoadingScreen()
                    viewModel.errorMessage != null -> MagazynErrorScreen(viewModel.errorMessage!!)
                    viewModel.filteredMagazynProbki.isEmpty() -> EmptyMagazynScreen()
                    else -> MagazynList(
                        probki = viewModel.filteredMagazynProbki,
                        isEditMode = viewModel.isEditMode,
                        onSave = { numer, sklad, szerokosc, ilosc, uwagi, dataProdukcji ->
                            viewModel.saveMagazynData(numer, sklad, szerokosc, ilosc, uwagi, dataProdukcji)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MagazynTopBar(
    isEditMode: Boolean,
    onToggleEditMode: () -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    TopAppBar(
        backgroundColor = AppColors.Primary,
        contentColor = AppColors.OnPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Magazyn próbek",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Szukaj...") },
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.OnPrimary,
                    backgroundColor = AppColors.Primary,
                    cursorColor = AppColors.OnPrimary,
                    focusedBorderColor = AppColors.OnPrimary,
                    unfocusedBorderColor = AppColors.OnPrimary.copy(alpha = 0.5f)
                ),
                singleLine = true
            )

            Button(
                onClick = onToggleEditMode,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isEditMode) AppColors.Secondary else AppColors.Surface
                )
            ) {
                Text(if (isEditMode) "Zakończ edycję" else "Tryb edycji")
            }
        }
    }
}

@Composable
fun EmptyMagazynScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Brak próbek w magazynie",
            style = MaterialTheme.typography.h6,
            color = AppColors.OnBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MagazynLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = AppColors.Primary)
            Text(
                text = "Ładowanie stanów magazynowych...",
                style = MaterialTheme.typography.body1,
                color = AppColors.OnBackground
            )
        }
    }
}

@Composable
fun MagazynErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️ Błąd ładowania magazynu",
                style = MaterialTheme.typography.h6,
                color = AppColors.Error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                color = AppColors.OnBackground
            )
        }
    }
}