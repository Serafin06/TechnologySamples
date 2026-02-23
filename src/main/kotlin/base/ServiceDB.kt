package base

import dataBase.Technologia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import report.RaportFilter
import report.ReportDTO
import java.time.format.DateTimeFormatter
import report.StatusConverter
import java.time.LocalDateTime

/**
 * Interface serwisu - Single Responsibility Principle
 */
interface ProbkaService {
    fun getProbki(monthBack: Long = 6): List<ProbkaDTO>
    fun saveTechnologiaKolumny(numer: Int, k1: String?, k2: String?, k3: String?, k4: String?): Boolean
    fun updateFlag(numer: Int, flagType: FlagType, value: Boolean): Boolean
    fun initializeProduceFlags()
    fun getAvailableKontrahenci(): List<String>
    fun testConnection(): Boolean
    suspend fun getMagazynProbki(): List<MagazynDTO>
    suspend fun getAvailableZOForMagazyn(): List<ZOPodpowiedzDTO>
    suspend fun saveMagazynData(
        numer: Int,
        skladMag: String?,
        szerokoscMag: String?,
        iloscMag: String?,
        uwagiMag: String?,
        dataProdukcjiMag: LocalDateTime?
    )
}

enum class FlagType {
    SEND, TESTED
}

/**
 * Implementacja serwisu - logika biznesowa
 */
class ProbkaServiceImpl(
    private val repository: ProbkaRepository,
    private val mapper: ProbkaMapper,
    private val statusResolver: StatusResolver
) : ProbkaService {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun getProbki(monthBack: Long): List<ProbkaDTO> {
        // Zamiast wielu zapytań, wywołujemy jedno nowe
        val probkiZO = repository.findProbkiWithDetails(monthBack)

        // Mapowanie jest teraz znacznie szybsze, bo wszystkie dane są już załadowane
        return probkiZO.map { zo ->
            mapper.toProbkaDTO(zo, statusResolver)
        }
    }

    override fun saveTechnologiaKolumny(
        numer: Int,
        k1: String?,
        k2: String?,
        k3: String?,
        k4: String?,
    ): Boolean {
        return try {

            val existing = repository.findTechnologiaByNumers(listOf(numer))[numer]

            val technologia = existing?.copy(
                opis = k1,
                dodatkoweInfo = k2,
                uwagi = k3,
                testy = k4,
            ) ?: Technologia(
                numer = numer,
                opis = k1,
                dodatkoweInfo = k2,
                uwagi = k3,
                testy = k4,
            )

            if (existing == null) {
                repository.batchInsertTechnologia(listOf(technologia))
            } else {
                repository.batchUpdateTechnologia(listOf(technologia))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun updateFlag(numer: Int, flagType: FlagType, value: Boolean): Boolean {
        return try {
            val existing = repository.findTechnologiaByNumers(listOf(numer))[numer]

            val technologia = existing?.apply {
                when (flagType) {
                    FlagType.SEND -> send = value
                    FlagType.TESTED -> tested = value
                }
            } ?: Technologia(
                numer = numer,
                send = if (flagType == FlagType.SEND) value else null,
                tested = if (flagType == FlagType.TESTED) value else null
            )

            if (existing == null) {
                repository.batchInsertTechnologia(listOf(technologia))
            } else {
                repository.batchUpdateTechnologia(listOf(technologia))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun initializeProduceFlags() {

        // 1. Pobierz tylko numery z ich stanami - lżejsze zapytanie
        val probkiData = repository.findProbkiStanOnly(monthsBack = 12)

        /// Pobierz istniejące Technologie w jednym zapytaniu
        val existingTech = repository.findTechnologiaByNumers(probkiData.keys)

        val toInsert = mutableListOf<Technologia>()
        val toUpdate = mutableListOf<Technologia>()

        probkiData.forEach { (numer, stan) ->
            val produceValue = when (stan) {
                0.toByte() -> true
                1.toByte(), 2.toByte() -> false
                else -> null
            }

            val existing = existingTech[numer]


            // 🆕 PUNKT 0: Utwórz nowy rekord TYLKO WTEDY, gdy próbka ma status
            // Planowane (2) lub Do Realizacji (1), co przekłada się na produceValue = false.
            // Jeśli produceValue == true (Zrealizowane) lub produceValue == null, ignorujemy.

            if (existing == null && produceValue == false) {
                toInsert.add(Technologia(numer = numer, produce = false, send = null, tested = null))

                // 🔄 PUNKT 1 & 2: Aktualizuj istniejący rekord

                // Dodatkowe sprawdzenie, czy aktualizacja z false na true jest dozwolona.
                // (technologia.produce != produceValue) gwarantuje, że nie aktualizujemy
                // ze stanu null na true, ponieważ ten przypadek został odrzucony powyżej.
                // Tutaj obsługujemy głównie przejście: false (w Technologia) -> true (z ZO).

            } else if (existing != null && existing.produce != produceValue) {
                toUpdate.add(
                    existing.copy(
                        produce = produceValue,
                        send = if (produceValue == true && existing.send == null) false else existing.send,
                        tested = if (produceValue == true && existing.tested == null) false else existing.tested
                    )
                )
            }
        }

        // Bulk insert/update
        if (toInsert.isNotEmpty()) repository.batchInsertTechnologia(toInsert)
        if (toUpdate.isNotEmpty()) repository.batchUpdateTechnologia(toUpdate)
    }

    override fun getAvailableKontrahenci(): List<String> {

        val probkiZO = repository.findProbkiWithDetails(monthsBack = 6)
        val uniqueIds = probkiZO.mapNotNull { it.idKontrahenta }.distinct()

        val kontrahenciMap = repository.findKontrahenciByIds(uniqueIds)

        return kontrahenciMap.values
            .mapNotNull { it.nazwa }
            .distinct()
            .sorted()
    }

    override fun testConnection(): Boolean {
        return try {
            repository.testConnection()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getMagazynProbki(): List<MagazynDTO> {
        return withContext(Dispatchers.IO) {
            repository.getAllMagazynProbki()
        }
    }

    override suspend fun getAvailableZOForMagazyn(): List<ZOPodpowiedzDTO> {
        return withContext(Dispatchers.IO) {
            repository.getAvailableZOForMagazyn()
        }
    }

    override suspend fun saveMagazynData(
        numer: Int,
        skladMag: String?,
        szerokoscMag: String?,
        iloscMag: String?,
        uwagiMag: String?,
        dataProdukcjiMag: LocalDateTime?
    ) {
        withContext(Dispatchers.IO) {
            repository.saveMagazynData(numer, skladMag, szerokoscMag, iloscMag, uwagiMag, dataProdukcjiMag)
        }
    }
}