package ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.PathHitTester
import base.FlagType
import base.ProbkaDTO
import base.ProbkaService
import kotlinx.coroutines.*
import java.time.LocalDateTime

enum class ConnectionStatus {
    CONNECTED,      // Zielony
    DISCONNECTED,   // Czerwony
    CHECKING        // Szary
}


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
                loadingMessage = "Inicjalizacja flag..."
                loadingProgress = 0.2f

                // Najpierw zainicjalizuj flagi
                withContext(Dispatchers.IO) {
                    probkaService.initializeProduceFlags()
                }

                loadingMessage = "Ładowanie próbek ZO..."
                loadingProgress = 0.4f

                probki = withContext(Dispatchers.IO) {
                    val monthsNonNull = filterState.dateRange.months ?: 6L
                    probkaService.getProbki(monthsNonNull)
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
            val matchesSearch = if (filterState.searchQuery.isBlank()) {
                true
            } else {
                val query = filterState.searchQuery.lowercase()
                probka.numer.toString().contains(query) ||
                        probka.art?.lowercase()?.contains(query) == true ||
                        probka.receptura?.lowercase()?.contains(query) == true
            }

            val matchesOddzial = filterState.oddzial?.let {
                probka.oddzialNazwa == it
            } ?: true

            val matchesStanZO = filterState.stanZO?.let {
                probka.statusZO?.stan == it
            } ?: true

            val matchesStanZK = filterState.stanZK?.let {
                probka.statusZK?.stan == it
            } ?: true

            val matchesStanZD = filterState.stanZD?.let {
                probka.statusZD?.stan == it
            } ?: true

            val matchesStanZL = filterState.stanZL?.let { expectedStan ->
                probka.statusZL?.any { it.stan == expectedStan } ?: false
            } ?: true

            matchesSearch && matchesOddzial && matchesStanZO && matchesStanZK && matchesStanZD && matchesStanZL
        }
    }

    fun saveTechnologiaKolumny(numer: Int,
                        k1: String?, k2: String?, k3: String?, k4: String?) {
        coroutineScope.launch {
            probkaService.saveTechnologiaKolumny(numer, k1, k2, k3, k4)
            loadProbki() // Odśwież dane
        }
    }
    fun saveTechnologiaKolumnyAsync(
        numer: Int,
        k1: String?,
        k2: String?,
        k3: String?,
        k4: String?,
    ) {
        // Zapis w tle, bez blokowania UI
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveTechnologiaKolumny(numer, k1, k2, k3, k4)

                // Odśwież tylko tę jedną próbkę zamiast wszystkich
                val updated = probkaService.getProbkaDetails(numer)
                if (updated != null) {
                    withContext(Dispatchers.Main) {
                        val index = probki.indexOfFirst {
                            it.numer == numer
                        }
                        if (index >= 0) {
                            probki = probki.toMutableList().apply {
                                set(index, updated)
                            }
                            applyFilters()
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Błąd zapisu: ${e.message}"
            }
        }
    }

    var connectionStatus by mutableStateOf(ConnectionStatus.CHECKING)
        private set

    var lastConnectionCheck by mutableStateOf<LocalDateTime?>(null)
        private set

    private val connectionCheckScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        // Uruchom automatyczne sprawdzanie co 5 minut
        startConnectionMonitoring()
    }

    private fun startConnectionMonitoring() {
        connectionCheckScope.launch {
            while (true) {
                checkDatabaseConnection()
                delay(5 * 60 * 1000L) // 5 minut
            }
        }
    }

    fun checkDatabaseConnection() {
        connectionCheckScope.launch {
            connectionStatus = ConnectionStatus.CHECKING

            try {
                val isConnected = probkaService.testConnection()
                connectionStatus = if (isConnected) {
                    ConnectionStatus.CONNECTED
                } else {
                    ConnectionStatus.DISCONNECTED
                }
                lastConnectionCheck = LocalDateTime.now()
            } catch (e: Exception) {
                connectionStatus = ConnectionStatus.DISCONNECTED
                lastConnectionCheck = LocalDateTime.now()
            }
        }
    }

    fun dispose() {
        coroutineScope.cancel()
        connectionCheckScope.cancel()
    }

    fun updateFlagAsync(numer: Int,flagType: FlagType, value: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.updateFlag(numer, flagType, value)

                // Odśwież tylko tę próbkę
                val updated = probkaService.getProbkaDetails(numer)
                if (updated != null) {
                    withContext(Dispatchers.Main) {
                        val index = probki.indexOfFirst {
                            it.numer == numer
                        }
                        if (index >= 0) {
                            probki = probki.toMutableList().apply {
                                set(index, updated)
                            }
                            applyFilters()
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Błąd aktualizacji flagi: ${e.message}"
            }
        }
    }
}