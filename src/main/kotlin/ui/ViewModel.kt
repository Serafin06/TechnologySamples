package ui

import androidx.compose.runtime.*
import base.FlagType
import base.ProbkaDTO
import base.ProbkaService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import report.ExportType
import report.generujRaportAkcja
import java.time.LocalDateTime

enum class ConnectionStatus {
    CONNECTED,      // Zielony
    DISCONNECTED,   // Czerwony
    CHECKING        // Szary
}

class ProbkiViewModel(val probkaService: ProbkaService) {

    // Główny coroutine scope dla operacji asynchronicznych
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Specjalny job do zarządzania zadaniem filtrowania, aby można je było anulować
    private val filterJob = SupervisorJob()

    // --- STANY APLIKACJI ---

    var loadingProgress by mutableStateOf(0f)
        private set

    var loadingMessage by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Główna lista wszystkich próbek (ładowana raz z bazy)
    var probki by mutableStateOf<List<ProbkaDTO>>(emptyList())
        private set

    // Lista filtrowana, wyświetlana w UI (aktualizowana w tle)
    var filteredProbki by mutableStateOf<List<ProbkaDTO>>(emptyList())
        private set

    // Stan filtra, teraz jako MutableStateFlow, co ułatwia reagowanie na zmiany
    private val _filterStateFlow = MutableStateFlow(FilterState())
    val filterStateFlow = _filterStateFlow.asStateFlow()

    // Zachowaj referencję do aktualnego stanu filtra dla łatwego dostępu
    var currentFilterState: FilterState
        get() = _filterStateFlow.value
        private set(value) { _filterStateFlow.value = value }

    var connectionStatus by mutableStateOf(ConnectionStatus.CHECKING)
        private set

    var lastConnectionCheck by mutableStateOf<LocalDateTime?>(null)
        private set

    private val connectionCheckScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var availableKontrahenci by mutableStateOf<List<String>>(emptyList())
        private set

    // Nowy stan do przechowywania wiadomości o eksporcie
    var exportMessage by mutableStateOf<String?>(null)
        private set

    // --- INICJALIZACJA ---

    init {
        // Uruchom nasłuchiwanie na zmiany filtra i monitorowanie połączenia
        startFiltering()
        startConnectionMonitoring()
    }

    // --- GŁÓWNA LOGIKA ---

    fun loadProbki() {
        coroutineScope.launch {
            isLoading = true
            loadingProgress = 0f
            errorMessage = null

            try {
                loadingMessage = "Inicjalizacja flag..."
                loadingProgress = 0.2f

                val monthsNonNull = currentFilterState.dateRange.months ?: 6L

                // Równoległe wykonanie inicjalizacji i ładowania danych
                val initDeferred = async(Dispatchers.IO) {
                    probkaService.initializeProduceFlags()
                }
                val probkiDeferred = async(Dispatchers.IO) {
                    probkaService.getProbki(monthsNonNull)
                }
                val kontrahenciDeferred = async(Dispatchers.IO) {
                    probkaService.getAvailableKontrahenci()
                }

                initDeferred.await()
                loadingProgress = 0.4f

                loadingMessage = "Ładowanie próbek ZO..."
                probki = probkiDeferred.await()
                availableKontrahenci = kontrahenciDeferred.await()

                loadingMessage = "Przetwarzanie danych..."
                loadingProgress = 0.8f

                triggerFiltering()
                loadingProgress = 1f

            } catch (e: Exception) {
                errorMessage = "Błąd ładowania danych: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- LOGIKA FILTROWANIA ---

    private fun startFiltering() {
        coroutineScope.launch {
            // Nasłuchujemy na zmiany w stanie filtra
            filterStateFlow
                .debounce(300) // Czekamy 300ms po ostatniej zmianie
                .distinctUntilChanged() // Filtrujemy tylko, jeśli stan faktycznie się zmienił
                .collect {
                    // Za każdym razem, gdy filtr się zmieni, uruchamiamy logikę filtrowania
                    triggerFiltering()
                }
        }
    }

    private fun triggerFiltering() {
        // Anulujemy poprzednie zadanie filtrowania, jeśli wciąż trwa
        filterJob.cancelChildren()

        // Uruchamiamy nowe zadanie filtrowania w wątku tła (Dispatchers.Default)
        coroutineScope.launch(filterJob) {
            val newFiltered = probki.filter { probka ->
                val matchesSearch = if (currentFilterState.searchQuery.isBlank()) {
                    true
                } else {
                    val query = currentFilterState.searchQuery.lowercase()
                    probka.numer.toString().contains(query) ||
                            probka.art?.lowercase()?.contains(query) == true ||
                            probka.receptura?.lowercase()?.contains(query) == true
                }

                val matchesOddzial = currentFilterState.oddzial?.let {
                    probka.oddzialNazwa == it
                } ?: true

                val matchesKontrahent = if (currentFilterState.selectedKontrahenci.isEmpty()) {
                    true
                } else {
                    currentFilterState.selectedKontrahenci.contains(probka.kontrahentNazwa)
                }

                val matchesStanZO = if (currentFilterState.selectedStatusZO.isEmpty()) {
                    true
                } else {
                    probka.statusZO?.stan in currentFilterState.selectedStatusZO
                }

                val matchesStanZK = if (currentFilterState.selectedStatusZK.isEmpty()) {
                    true
                } else {
                    probka.statusZK?.stan in currentFilterState.selectedStatusZK
                }

                val matchesStanZD = if (currentFilterState.selectedStatusZD.isEmpty()) {
                    true
                } else {
                    probka.statusZD?.stan in currentFilterState.selectedStatusZD
                }

                val matchesStanZL = if (currentFilterState.selectedStatusZL.isEmpty()) {
                    true
                } else {
                    probka.statusZL?.any { it.stan in currentFilterState.selectedStatusZL } == true
                }

                matchesSearch && matchesOddzial && matchesKontrahent &&
                        matchesStanZO && matchesStanZK && matchesStanZD && matchesStanZL
            }

            // Po zakończeniu filtrowania, aktualizujemy stan w głównym wątku UI
            withContext(Dispatchers.Main) {
                filteredProbki = newFiltered
            }
        }
    }

    // Funkcja do wywoływania z UI, gdy użytkownik zmieni filtr
    fun updateFilter(newFilter: FilterState) {
        _filterStateFlow.value = newFilter
    }

    // --- AKTUALIZACJE DANYCH (optymistyczne) ---

    fun saveTechnologiaKolumnyAsync(
        numer: Int,
        k1: String?,
        k2: String?,
        k3: String?,
        k4: String?,
    ) {
        val index = probki.indexOfFirst { it.numer == numer }
        if (index == -1) return

        val currentProbka = probki[index]
        val updatedProbka = currentProbka.copy(
            opis = k1,
            dodtkoweInformacje = k2,
            uwagi = k3,
            testy = k4
        )

        // Natychmiast zaktualizuj UI
        probki = probki.toMutableList().apply { set(index, updatedProbka) }
        triggerFiltering() // Używamy nowej funkcji

        // Zapisz zmianę w tle
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveTechnologiaKolumny(numer, k1, k2, k3, k4)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd zapisu: ${e.message}"
                    probki = probki.toMutableList().apply { set(index, currentProbka) }
                    triggerFiltering()
                }
            }
        }
    }

    fun updateFlagAsync(numer: Int, flagType: FlagType, value: Boolean) {
        val index = probki.indexOfFirst { it.numer == numer }
        if (index == -1) return

        val currentProbka = probki[index]
        val updatedProbka = when (flagType) {
            FlagType.SEND -> currentProbka.copy(send = value)
            FlagType.TESTED -> currentProbka.copy(tested = value)
        }

        // Natychmiast zaktualizuj UI
        probki = probki.toMutableList().apply { set(index, updatedProbka) }
        triggerFiltering()

        // Zapisz zmianę w tle
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.updateFlag(numer, flagType, value)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd aktualizacji flagi: ${e.message}"
                    probki = probki.toMutableList().apply { set(index, currentProbka) }
                    triggerFiltering()
                }
            }
        }
    }

    // --- EKSPORT DANYCH ---

    fun exportToExcel() {
        // Wywołujemy akcję raportu, przekazując serwis i callback
        generujRaportAkcja(
            scope = coroutineScope,
            type = ExportType.EXCEL,
            probkaService = probkaService,
            onComplete = { success, path ->
                // Aktualizujemy stan, który zostanie wyświetlony w UI
                exportMessage = if (success) {
                    "Sukces! Raport zapisano w:\n$path"
                } else {
                    "Błąd! Nie udało się zapisać raportu."
                }
            }
        )
    }

    fun exportToPdf() {
        // To samo dla PDF
        generujRaportAkcja(
            scope = coroutineScope,
            type = ExportType.PDF,
            probkaService = probkaService,
            onComplete = { success, path ->
                exportMessage = if (success) {
                    "Sukces! Raport zapisano w:\n$path"
                } else {
                    "Błąd! Nie udało się zapisać raportu."
                }
            }
        )
    }

    // --- POZOSTAŁE FUNKCJE ---

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
                connectionStatus = if (isConnected) ConnectionStatus.CONNECTED else ConnectionStatus.DISCONNECTED
                lastConnectionCheck = LocalDateTime.now()
            } catch (e: Exception) {
                connectionStatus = ConnectionStatus.DISCONNECTED
                lastConnectionCheck = LocalDateTime.now()
            }
        }
    }

    // Funkcja do czyszczenia wiadomości po zamknięciu dialogu
    fun clearExportMessage() {
        exportMessage = null
    }

    fun dispose() {
        coroutineScope.cancel()
        connectionCheckScope.cancel()
    }

}