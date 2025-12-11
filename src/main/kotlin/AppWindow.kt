
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

// Definiujemy stany, w których może znajdować się aplikacja
enum class AppState {
    Loading, // Ekran powitalny
    Ready,   // Główny ekran aplikacji
    Error    // Ekran błędu krytycznego
}

// Suspend funkcja, która wykonuje całą ciężką pracę inicjalizacji
// Zwraca w pełni przygotowany ViewModel lub rzuca wyjątek
suspend fun initializeApp(): ProbkiViewModel {
    // Te operacje mogą być czasochłonne, więc wykonujemy je w tle
    return withContext(Dispatchers.IO) {
        val sessionFactory = HibernateConfig.sessionFactory
        val probkaRepository = base.ProbkaRepositoryImpl(sessionFactory)
        val probkaService = ProbkaServiceFactory.createProbkaService(probkaRepository)
        val reportService = report.createReportService(probkaRepository)

        val viewModel = ProbkiViewModel(probkaService, reportService)

        // Czekamy, aż wszystkie dane zostaną załadowane do ViewModelu
        viewModel.loadProbki()

        viewModel
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Załaduj i wyświetl swój obrazek
            Image(
                painter = painterResource("drawable/logo2.png"),
                contentDescription = "Logo Aplikacji",
                modifier = Modifier.size(700.dp)
            )

            Text(
                text = "Technologia - Zarządzanie Próbkami",
                style = MaterialTheme.typography.h5,
                color = AppColors.Primary
            )

            CircularProgressIndicator()
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
    // Stan aplikacji, domyślnie ustawiony na ładowanie
    val appState = remember { mutableStateOf(AppState.Loading) }
    // Stan do przechowywania ViewModelu po jego utworzeniu
    val viewModelState = remember { mutableStateOf<ProbkiViewModel?>(null) }
    // Stan do przechowywania ewentualnego błędu
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Uruchamiamy inicjalizację tylko raz, przy starcie komponentu
    LaunchedEffect(Unit) {
        try {
            val viewModel = initializeApp()
            viewModelState.value = viewModel
            appState.value = AppState.Ready
        } catch (e: Exception) {
            errorMessage.value = e.message
            appState.value = AppState.Error
        }
    }

    // Na podstawie stanu aplikacji wyświetlamy odpowiedni ekran
    when (appState.value) {
        AppState.Loading -> SplashScreen()
        AppState.Ready -> {
            // Gdy stan jest Ready, mamy pewność, że viewModelState nie jest null
            viewModelState.value?.let { viewModel ->
                ProbkiScreen(viewModel)
            }
        }
        AppState.Error -> {
            errorMessage.value?.let { message ->
                ErrorScreen(message)
            }
        }
    }
}