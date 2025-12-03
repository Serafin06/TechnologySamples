package base

import dataBase.Technologia
import report.RaportFilter
import report.ReportDTO
import java.time.format.DateTimeFormatter
import report.StatusConverter

/**
 * Interface serwisu - Single Responsibility Principle
 */
interface ProbkaService {
    fun getProbki(monthBack: Long = 6): List<ProbkaDTO>
    fun getProbkaDetails(numer: Int): ProbkaDTO?
    fun saveTechnologiaKolumny(numer: Int, k1: String?, k2: String?, k3: String?, k4: String?): Boolean
    fun updateFlag(numer: Int, flagType: FlagType, value: Boolean): Boolean
    fun initializeProduceFlags()
    fun getAvailableKontrahenci(): List<String>
    fun getDaneDoRaportu(filter: RaportFilter): List<ReportDTO>
    fun testConnection(): Boolean
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

    override fun getProbkaDetails(numer: Int): ProbkaDTO? {
        // Jedno zoptymalizowane zapytanie pobierające WSZYSTKIE dane dla jednej próbki
        val zo = repository.findProbkaWithDetailsByNumer(numer) ?: return null

        // Użycie nowej, uproszczonej metody mappera
        return mapper.toProbkaDTO(zo, statusResolver)
    }


    override fun saveTechnologiaKolumny(
        numer: Int,
        k1: String?,
        k2: String?,
        k3: String?,
        k4: String?,
    ): Boolean {
        return try {
            val existing = repository.findTechnologiaNumer(numer)

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

            repository.saveTechnologia(technologia)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun updateFlag(numer: Int, flagType: FlagType, value: Boolean): Boolean {
        return try {
            val existing = repository.findTechnologiaNumer(numer)

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

            repository.saveTechnologia(technologia)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun initializeProduceFlags() {

        // 1. Pobierz wszystkie potrzebne dane w DWÓCH zapytaniach
        val allProbkiZO = repository.findProbkiZO() // Pobiera wszystkie próbki (proba=1)
        val allTechnologia = repository.findAllTechnologia()

        // 2. Stwórz mapę dla szybkiego dostępu (O(1)) zamiast pętli
        val technologiaMap = allTechnologia.associateBy { it.numer }

        // 3. Przygotuj listę obiektów do zapisania
        val entitiesToSave = mutableListOf<Technologia>()

        allProbkiZO.forEach { zo ->
            val produceValue = when (zo.stan) {
                0.toByte() -> true
                1.toByte(), 2.toByte() -> false
                else -> null
            }

            // 3. Logika zapisu/aktualizacji
            val technologia = technologiaMap[zo.numer]

            if (technologia == null) {
                // 🆕 PUNKT 0: Utwórz nowy rekord TYLKO WTEDY, gdy próbka ma status
                // Planowane (2) lub Do Realizacji (1), co przekłada się na produceValue = false.
                // Jeśli produceValue == true (Zrealizowane) lub produceValue == null, ignorujemy.
                if (produceValue == false) {
                    entitiesToSave.add(
                        Technologia(
                            numer = zo.numer,
                            produce = false,
                            send = null,
                            tested = null
                        )
                    )
                }
            } else if (technologia.produce != produceValue) {
                // 🔄 PUNKT 1 & 2: Aktualizuj istniejący rekord

                // Dodatkowe sprawdzenie, czy aktualizacja z false na true jest dozwolona.
                // (technologia.produce != produceValue) gwarantuje, że nie aktualizujemy
                // ze stanu null na true, ponieważ ten przypadek został odrzucony powyżej.
                // Tutaj obsługujemy głównie przejście: false (w Technologia) -> true (z ZO).
                val updated = technologia.copy(
                    produce = produceValue,
                    // Flagi send/tested ustawiamy na false tylko wtedy, gdy przejście
                    // na produce=true się odbywa, A poprzednie wartości były null.
                    send = if (produceValue == true && technologia.send == null) false else technologia.send,
                    tested = if (produceValue == true && technologia.tested == null) false else technologia.tested
                )
                entitiesToSave.add(updated)
            }
        }

        // 4. Zapisz WSZYSTKIE zmiany w JEDNEJ operacji bazodanowej
        if (entitiesToSave.isNotEmpty()) {
            repository.saveAllTechnologia(entitiesToSave)
        }
    }

    override fun getAvailableKontrahenci(): List<String> {
        val probkiZO = repository.findProbkiZO()
        val uniqueIds = probkiZO.map { it.idKontrahenta }.distinct()

        // Użyj istniejącej metody findKontrahenciByIds
        val kontrahenciMap = repository.findKontrahenciByIds(uniqueIds)

        return kontrahenciMap.values
            .mapNotNull { it.nazwa }
            .distinct()
            .sorted()
    }

    override fun getDaneDoRaportu(filter: RaportFilter): List<report.ReportDTO> {
        // 1. Wywołanie zoptymalizowanego Repo (JOIN FETCH)
        val daneZBazy = repository.findReportData(filter)

        // 2. Mapowanie na ReportDTO (błyskawicznie)
        return daneZBazy.map { zo ->
            // Używamy firstOrNull(), ponieważ Set nie ma indeksów
            val technologia = zo.technologia
            val zd = zo.statusZD?.firstOrNull()
            val zk = zo.statusZK?.firstOrNull()

            // W przypadku ZL (gdzie potrzebujesz ZL1 i ZL2) musisz posortować Set, aby uzyskać konsekwentne wyniki
            val sortedZLL = zo.statusZL?.sortedBy { it.id } // Zakładając, że Encja ZL ma pole ID
            val zl1 = sortedZLL?.getOrNull(0)
            val zl2 = sortedZLL?.getOrNull(1)

            report.ReportDTO(
                numerZlecenia = zo.numer,
                kontrahent = zo.kontrahent?.nazwa ?: "Nieznany",
                nazwaProbki = zo.opis1 ?: "",

                // Statusy (używamy statusu z ZO dla statusZO, ale w ReportDTO jest on usuwany w Excelu,
                // natomiast w tym DTO musi zostać, jeśli jest potrzebny w innych miejscach)
                statusZO = StatusConverter.mapToSymbol(zo.stan),
                terminZO = zo.terminZak?.format(dateFormatter) ?: "-",
                statusZD = StatusConverter.mapToSymbol(zd?.stan),
                statusZK = StatusConverter.mapToSymbol(zk?.stan),
                statusZL1 = StatusConverter.mapToSymbol(zl1?.stan),
                statusZL2 = StatusConverter.mapToSymbol(zl2?.stan),

                // 💡 POPRAWKA: DANE TECHNICZNE POCHODZĄ Z ENCJI ZO
                art = zo.art ?: "",
                receptura = zo.receptura1 ?: "",
                szerokosc = zo.szerokosc1?.toString() ?: "-", // Zgadza się z Mapperem (zo.szerokosc1)
                grubosc1 = zo.grubosc11 ?: "",
                grubosc2 = zo.grubosc21 ?: "",
                grubosc3 = zo.grubosc31 ?: "",

                // Informacje dodatkowe pochodzą z Technologii (zgadza się)
                opis = technologia?.opis ?: "",
                dodatkoweInfo = technologia?.dodatkoweInfo ?: ""
            )
        }
    }


    override fun testConnection(): Boolean {
        return try {
            repository.testConnection()
            true
        } catch (e: Exception) {
            false
        }
    }
}