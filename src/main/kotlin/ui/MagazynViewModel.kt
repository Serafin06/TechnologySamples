package ui

import androidx.compose.runtime.*
import base.MagazynDTO
import base.ProbkaDTO
import base.ProbkaService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

/**
 * ViewModel dla modułu magazynu próbek
 * Zarządza stanami magazynowymi i ich edycją
 */

class MagazynViewModel(private val probkaService: ProbkaService) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var magazynProbki by mutableStateOf<List<MagazynDTO>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isEditMode by mutableStateOf(false)
        private set

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    var foundProbka by mutableStateOf<ProbkaDTO?>(null)
        private set

    var isSearching by mutableStateOf(false)
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    var searchError by mutableStateOf<String?>(null)
        private set

    private val _skladFilter = MutableStateFlow("")
    val skladFilter = _skladFilter.asStateFlow()


    val filteredMagazynProbki: StateFlow<List<MagazynDTO>> = combine(
        _searchQuery,
        _skladFilter,
        snapshotFlow { magazynProbki }
    ) { query, skladFilterValue, lista ->
        val q = query.lowercase().trim()
        val s = skladFilterValue.lowercase().trim()

        lista.filter { probka ->
            // 1. Filtr ogólny (szukajka po numerze i kontrahencie)
            val matchesQuery = q.isBlank() ||
                    probka.numer.toString().contains(q) ||
                    probka.kontrahentNazwa.lowercase().contains(q)

            // 2. Precyzyjny filtr składu/struktury
            val matchesSklad = s.isBlank() || run {
                // Pobieramy tekst ze składu i struktury (skoro to dla Ciebie to samo)
                val tekstDoPrzeszukania = "${probka.skladMag ?: ""} ${probka.strukturaMag ?: ""}".lowercase()

                // Dzielimy tekst na konkretne materiały (separatory: spacja, ukośnik, przecinek, procent)
                val materialy = tekstDoPrzeszukania.split(Regex("[\\s/%,.-]+")).filter { it.isNotBlank() }

                // Sprawdzamy czy którykolwiek materiał ZACZYNA SIĘ od wpisanej frazy
                // Dzięki temu "PET" znajdzie "PET", ale nie znajdzie "PE"
                materialy.any { m -> m.startsWith(s) }
            }

            matchesQuery && matchesSklad
        }.sortedByDescending { it.numer }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSkladFilter(value: String) {
        _skladFilter.value = value
    }

    suspend fun loadMagazynProbki() {
        withContext(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                magazynProbki = probkaService.getMagazynProbki()
                // USUNIĘTO: triggerFiltering() - niepotrzebne
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd ładowania magazynu: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun searchProbka(numerInput: String) {
        val numer = numerInput.toIntOrNull()
        if (numer == null) {
            errorMessage = "Wpisz poprawny numer."
            return
        }
        coroutineScope.launch {
            isSearching = true
            errorMessage = null
            foundProbka = null
            try {
                val result = probkaService.getProbkaByNumer(numer)
                if (result == null) searchError = "Nie znaleziono próbki nr $numer"
                else foundProbka = result
            } catch (e: Exception) {
                errorMessage = "Błąd wyszukiwania: ${e.message}"
            } finally {
                isSearching = false
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
        strukturaMag: String?,
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
            strukturaMag = strukturaMag,
            skladMag = skladMag,
            szerokoscMag = szerokoscMag,
            iloscMag = iloscMag,
            uwagiMag = uwagiMag,
            dataProdukcjiMag = dataProdukcjiMag,
            dataAktualizacjiMag = LocalDateTime.now()
        )

        // Aktualizacja tej listy automatycznie "odpali" filtrowanie dzięki snapshotFlow
        magazynProbki = magazynProbki.toMutableList().apply { set(index, updated) }

        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveMagazynData(numer, strukturaMag, skladMag, szerokoscMag, iloscMag, uwagiMag, dataProdukcjiMag)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd zapisu: ${e.message}"
                    magazynProbki = magazynProbki.toMutableList().apply { set(index, current) }
                }
            }
        }
    }

    fun openAddDialog() {
        showAddDialog = true
        foundProbka = null
        searchError = null
    }

    fun closeAddDialog() {
        showAddDialog = false
        foundProbka = null
    }

    fun addMagazynEntry(
        numer: Int,
        strukturaMag: String?,
        skladMag: String?,
        szerokoscMag: String?,
        iloscMag: String?,
        uwagiMag: String?,
        dataProdukcjiMag: LocalDateTime?
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveMagazynData(numer, strukturaMag, skladMag, szerokoscMag, iloscMag, uwagiMag, dataProdukcjiMag)
                loadMagazynProbki() // To odświeży magazynProbki i automatycznie wyzwoli filtr
                withContext(Dispatchers.Main) { showAddDialog = false }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { errorMessage = "Błąd dodawania: ${e.message}" }
            }
        }
    }

    fun deleteMagazynEntry(numer: Int) {
        val index = magazynProbki.indexOfFirst { it.numer == numer }
        if (index == -1) return

        val current = magazynProbki[index]
        magazynProbki = magazynProbki.toMutableList().apply { removeAt(index) }

        coroutineScope.launch(Dispatchers.IO) {
            try {
                probkaService.saveMagazynData(
                    numer = numer,
                    strukturaMag = current.strukturaMag,
                    skladMag = current.skladMag,
                    szerokoscMag = current.szerokoscMag,
                    iloscMag = null,
                    uwagiMag = current.uwagiMag,
                    dataProdukcjiMag = current.dataProdukcjiMag,
                    magAktywny = false
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Błąd usuwania: ${e.message}"
                    magazynProbki = magazynProbki.toMutableList().apply { add(index, current) }
                }
            }
        }
    }

    fun dispose() {
        coroutineScope.cancel()
    }
}