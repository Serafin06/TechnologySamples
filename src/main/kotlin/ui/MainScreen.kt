package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ProbkiScreen(viewModel: ProbkiViewModel) {

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
            topBar = { TopAppBar(connectionStatus = viewModel.connectionStatus,
                    lastCheck = viewModel.lastConnectionCheck,
                    onConnectionCheck = { viewModel.checkDatabaseConnection() }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background)
            ) {
                // Filtry
                FilterPanel(
                    coroutineScope = rememberCoroutineScope(),
                    probkaService = viewModel.probkaService,
                    filterState = viewModel.filterState,
                    onFilterChange = { viewModel.updateFilter(it) },
                    onRefresh = { viewModel.loadProbki() } // Usuń scope.launch
                )

                // Zawartość
                when {
                    viewModel.isLoading -> LoadingScreen(
                        progress = viewModel.loadingProgress,
                        message = viewModel.loadingMessage
                    )
                    viewModel.errorMessage != null -> ErrorScreen(viewModel.errorMessage!!)
                    viewModel.filteredProbki.isEmpty() -> EmptyScreen()
                    else -> ProbkiList(
                        probki = viewModel.filteredProbki,
                        onTechnologiaSave = { numer, k1, k2, k3, k4 ->
                            viewModel.saveTechnologiaKolumnyAsync(numer, k1, k2, k3, k4)
                        },
                        onFlagUpdate = { numer, flagType, value ->
                            viewModel.updateFlagAsync(numer, flagType, value)
                        }
                    )
                }
            }
        }
    }
}