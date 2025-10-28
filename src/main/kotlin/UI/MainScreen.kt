package pl.rafapp.techSam.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch



@Composable
fun ProbkiScreen(viewModel: ProbkiViewModel) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.loadProbki()
        }
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
            topBar = { TopAppBar() }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppColors.Background)
            ) {
                // Filtry
                FilterPanel(
                    filterState = viewModel.filterState,
                    onFilterChange = { viewModel.updateFilter(it) },
                    onRefresh = { scope.launch { viewModel.loadProbki() } }
                )

                // Zawartość
                when {
                    viewModel.isLoading -> LoadingScreen()
                    viewModel.errorMessage != null -> ErrorScreen(viewModel.errorMessage!!)
                    viewModel.filteredProbki.isEmpty() -> EmptyScreen()
                    else -> ProbkiList(viewModel.filteredProbki)
                }
            }
        }
    }
}