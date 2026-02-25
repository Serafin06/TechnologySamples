package ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import ui.AppColors
import ui.MagazynViewModel
import ui.dialog.AddMagazynDialog

@Composable
fun MagazynScreen(viewModel: MagazynViewModel) {

    val filteredList by viewModel.filteredMagazynProbki.collectAsState()

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
                    onSearchChange = { viewModel.updateSearchQuery(it) },
                    onAddClick = { viewModel.openAddDialog() },
                    strukturaFilter = viewModel.skladFilter.collectAsState().value,
                    onStrukruraFilterChange = { viewModel.updateSkladFilter(it) }
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
                    filteredList.isEmpty() -> EmptyMagazynScreen()
                    else -> MagazynList(
                        probki = filteredList,
                        isEditMode = viewModel.isEditMode,
                        onSave = { numer, struktura, sklad, szerokosc, ilosc, uwagi, dataProdukcji ->
                            viewModel.saveMagazynData(numer, struktura, sklad, szerokosc, ilosc, uwagi, dataProdukcji)
                        },
                        onDelete = { viewModel.deleteMagazynEntry(it) }
                    )
                }
            }

            if (viewModel.showAddDialog) {
                AddMagazynDialog(
                    // ZMIANA: Przekazujemy znalezioną próbkę i metody
                    foundProbka = viewModel.foundProbka,
                    isSearching = viewModel.isSearching,
                    searchError = viewModel.searchError,
                    onSearch = { viewModel.searchProbka(it) },
                    onDismiss = { viewModel.closeAddDialog() },
                    onConfirm = { numer, struktura, sklad, szerokosc, ilosc, uwagi, dataProdukcji ->
                        viewModel.addMagazynEntry(numer, struktura, sklad, szerokosc, ilosc, uwagi, dataProdukcji)
                    }
                )
            }
        }
    }
}

@Composable
fun MagazynTopBar(
    isEditMode: Boolean,
    onToggleEditMode: () -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddClick: () -> Unit,
    strukturaFilter: String,
    onStrukruraFilterChange: (String) -> Unit
) {
    TopAppBar(
        backgroundColor = AppColors.Primary,
        contentColor = AppColors.OnPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Magazyn próbek",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = strukturaFilter,
                onValueChange = onStrukruraFilterChange,
                placeholder = { Text("Filtr składu...") },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = AppColors.OnPrimary,
                    backgroundColor = AppColors.Primary,
                    cursorColor = AppColors.OnPrimary,
                    focusedBorderColor = AppColors.OnPrimary,
                    unfocusedBorderColor = AppColors.OnPrimary.copy(alpha = 0.5f)
                ),
                singleLine = true
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
                onClick = onAddClick,
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.Surface)
            ) {
                Text("+ Dodaj")
            }

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