package pl.rafapp.techSam.UI

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import pl.rafapp.techSam.Base.*


class ProbkiViewModel(private val probkaService: ProbkaService) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var loadingProgress by mutableStateOf(0f)
        private set

    var loadingMessage by mutableStateOf("")
        private set

    var probki by mutableStateOf<List<ProbkaDTO>>(emptyList())
        private set

    var filteredProbki by mutableStateOf<List<ProbkaDTO>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var filterState by mutableStateOf(FilterState())
        private set

    fun loadProbki() {
        coroutineScope.launch {
            isLoading = true
            loadingProgress = 0f
            errorMessage = null

            try {
                loadingMessage = "Ładowanie próbek ZO..."
                loadingProgress = 0.3f

                probki = withContext(Dispatchers.IO) {
                    probkaService.getProbki()
                }

                loadingMessage = "Przetwarzanie danych..."
                loadingProgress = 0.8f

                applyFilters()
                loadingProgress = 1f

            } catch (e: Exception) {
                errorMessage = "Błąd ładowania danych: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    fun updateFilter(newFilter: FilterState) {
        filterState = newFilter
        applyFilters()
    }

    private fun applyFilters() {
        filteredProbki = probki.filter { probka ->
            // Filtr wyszukiwania (numer, ART, receptura)
            val matchesSearch = if (filterState.searchQuery.isBlank()) {
                true
            } else {
                val query = filterState.searchQuery.lowercase()
                probka.numer.toString().contains(query) ||
                        probka.art?.lowercase()?.contains(query) == true ||
                        probka.receptura?.lowercase()?.contains(query) == true
            }

            // Filtr oddziału
            val matchesOddzial = filterState.oddzial?.let {
                probka.oddzialNazwa == it
            } ?: true

            // Filtr statusu ZO
            val matchesStanZO = filterState.stanZO?.let {
                probka.statusZO?.stan == it
            } ?: true

            // Filtr statusu ZK
            val matchesStanZK = filterState.stanZK?.let {
                probka.statusZK?.stan == it
            } ?: true

            // Filtr statusu ZD
            val matchesStanZD = filterState.stanZD?.let {
                probka.statusZD?.stan == it
            } ?: true

            matchesSearch && matchesOddzial && matchesStanZO && matchesStanZK && matchesStanZD
        }
    }
    fun dispose() {
        coroutineScope.cancel()
    }
}