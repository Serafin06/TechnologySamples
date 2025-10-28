package pl.rafapp.techSam.UI

import androidx.compose.runtime.*
import pl.rafapp.techSam.Base.*


class ProbkiViewModel(private val probkaService: ProbkaService) {

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

    suspend fun loadProbki() {
        isLoading = true
        errorMessage = null
        try {
            probki = probkaService.getProbki()
            applyFilters()
        } catch (e: Exception) {
            errorMessage = "Błąd ładowania danych: ${e.message}"
        } finally {
            isLoading = false
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
}