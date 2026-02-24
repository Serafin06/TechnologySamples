package ui

import androidx.compose.runtime.*
import base.MagazynDTO
import base.ProbkaService
import base.ZOPodpowiedzDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

/**
 * ViewModel dla modułu magazynu próbek
 * Zarządza stanami magazynowymi i ich edycją
 */

class MagazynViewModel(private val probkaService: ProbkaService) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Lista próbek w magazynie (tylko te z niepustymi danymi magazynowymi)
    var magazynProbki by mutableStateOf<List<MagazynDTO>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Tryb edycji
    var isEditMode by mutableStateOf(false)
        private set

    // Wyszukiwarka
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    var filteredMagazynProbki by mutableStateOf<List<MagazynDTO>>(emptyList())
        private set

    //dodawanie próbek do magazynu - potrzebne do wyboru ZO
    var availableZO by mutableStateOf<List<ZOPodpowiedzDTO>>(emptyList())
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    init {
        startFiltering()
    }

    suspend fun loadMagazynProbki() {
        withContext(Dispatchers.IO) {
            isLoading = true
            errorMessage = null

            try {
                magazynProbki = probkaService.getMagazynProbki()
                availableZO = probkaService.getAvailableZOForMagazyn()
                triggerFiltering()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd ładowania magazynu: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    private fun startFiltering() {
        coroutineScope.launch {
            searchQuery
                .debounce(300)
                .collect {
                    triggerFiltering()
                }
        }
    }

    private fun triggerFiltering() {
        coroutineScope.launch {
            val query = _searchQuery.value.lowercase()
            val filtered = if (query.isBlank()) {
                magazynProbki
            } else {
                magazynProbki.filter { probka ->
                    probka.numer.toString().contains(query) ||
                            probka.kontrahentNazwa.lowercase().contains(query) ||
                            probka.skladMag?.lowercase()?.contains(query) == true
                }
            }

            withContext(Dispatchers.Main) {
                filteredMagazynProbki = filtered
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleEditMode() {
        isEditMode = !isEditMode
    }

    fun saveMagazynData(
        numer: Int,
        skladMag: String?,
        szerokoscMag: String?,
        iloscMag: String?,
        uwagiMag: String?,
        dataProdukcjiMag: LocalDateTime?
    ) {
        val index = magazynProbki.indexOfFirst { it.numer == numer }
        if (index == -1) return

        val current = magazynProbki[index]
        val updated = current.copy(
            skladMag = skladMag,
            szerokoscMag = szerokoscMag,
            iloscMag = iloscMag,
            uwagiMag = uwagiMag,
            dataProdukcjiMag = dataProdukcjiMag,
            dataAktualizacjiMag = LocalDateTime.now()
        )

        // Optymistyczna aktualizacja UI
        magazynProbki = magazynProbki.toMutableList().apply { set(index, updated) }
        triggerFiltering()

        // Zapis w tle
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveMagazynData(numer, skladMag, szerokoscMag, iloscMag, uwagiMag, dataProdukcjiMag)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd zapisu: ${e.message}"
                    magazynProbki = magazynProbki.toMutableList().apply { set(index, current) }
                    triggerFiltering()
                }
            }
        }
    }

    fun openAddDialog() { showAddDialog = true }
    fun closeAddDialog() { showAddDialog = false }

    fun addMagazynEntry(
        numer: Int,
        skladMag: String?,
        szerokoscMag: String?,
        iloscMag: String?,
        uwagiMag: String?,
        dataProdukcjiMag: LocalDateTime?
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveMagazynData(numer, skladMag, szerokoscMag, iloscMag, uwagiMag, dataProdukcjiMag)
                loadMagazynProbki()
                withContext(Dispatchers.Main) { showAddDialog = false }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { errorMessage = "Błąd dodawania: ${e.message}" }
            }
        }
    }

    fun dispose() {
        coroutineScope.cancel()
    }
}




