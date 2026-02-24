import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import base.ProbkaServiceFactory
import dataBase.HibernateConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.AppColors
import ui.panels.ProbkiScreen
import ui.ProbkiViewModel
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import ui.MagazynViewModel
import ui.WitchLoadingEffect
import ui.panels.MagazynScreen

// Definiujemy stany, w których może znajdować się aplikacja
enum class AppState {
    Loading, // Ekran powitalny
    Ready,   // Główny ekran aplikacji
    Error    // Ekran błędu krytycznego
}

enum class AppTab {
    PROBKI,
    MAGAZYN
}

// Suspend funkcja, która wykonuje całą ciężką pracę inicjalizacji
// Zwraca w pełni przygotowany ViewModel lub rzuca wyjątek
suspend fun initializeApp(): Pair<ProbkiViewModel, MagazynViewModel> {
    return withContext(Dispatchers.IO) {
        val sessionFactory = HibernateConfig.sessionFactory
        val probkaRepository = base.ProbkaRepositoryImpl(sessionFactory)
        val probkaService = ProbkaServiceFactory.createProbkaService(probkaRepository)
        val reportService = report.createReportService(probkaRepository)

        val probkiViewModel = ProbkiViewModel(probkaService, reportService)
        val magazynViewModel = MagazynViewModel(probkaService)

        probkiViewModel.loadProbki()

        Pair(probkiViewModel, magazynViewModel)
    }
}

// Komponent ekranu powitalnego
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // WYŁĄCZNIK: Zmień na false, żeby ukryć wiedźmy
        WitchLoadingEffect(enabled = true)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource("drawable/logo2.png"),
                contentDescription = "Logo Aplikacji",
                modifier = Modifier.size(600.dp)
            )

            Text(
                text = "Technologia - Zarządzanie Próbkami",
                style = MaterialTheme.typography.h5,
                color = AppColors.Primary
            )
        }
    }
}

// Komponent ekranu błędu
@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Wystąpił krytyczny błąd podczas uruchamiania aplikacji:\n$message")
    }
}

// Główny komponent aplikacji, który zarządza stanem
@Composable
fun App() {
    val appState = remember { mutableStateOf(AppState.Loading) }
    // Zmieniony typ:
    val viewModelState = remember { mutableStateOf<Pair<ProbkiViewModel, MagazynViewModel>?>(null) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val viewModels = initializeApp()
            viewModelState.value = viewModels
            appState.value = AppState.Ready
        } catch (e: Exception) {
            errorMessage.value = e.message
            appState.value = AppState.Error
        }
    }

    when (appState.value) {
        AppState.Loading -> SplashScreen()
        AppState.Ready -> {
            // Zmieniony destructuring:
            viewModelState.value?.let { (probkiVM, magazynVM) ->
                var selectedTab by remember { mutableStateOf(AppTab.PROBKI) }

                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        backgroundColor = AppColors.Primary,
                        contentColor = Color.White
                    ) {
                        Tab(
                            selected = selectedTab == AppTab.PROBKI,
                            onClick = { selectedTab = AppTab.PROBKI },
                            text = { Text("Próbki") }
                        )
                        Tab(
                            selected = selectedTab == AppTab.MAGAZYN,
                            onClick = { selectedTab = AppTab.MAGAZYN },
                            text = { Text("Magazyn próbek") }
                        )
                    }

                    when (selectedTab) {
                        AppTab.PROBKI -> ProbkiScreen(probkiVM)
                        AppTab.MAGAZYN -> MagazynScreen(magazynVM)
                    }
                }
            }
        }
        AppState.Error -> {
            errorMessage.value?.let { message ->
                ErrorScreen(message)
            }
        }
    }
}