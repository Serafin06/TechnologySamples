package base

import dataBase.Technologia
import report.ReportDTO
import report.ReportRepository

/**
 * Interface serwisu - Single Responsibility Principle
 */
interface ProbkaService {
    fun getProbki(monthBack: Long = 6): List<ProbkaDTO>
    fun getProbkaDetails(numer: Int): ProbkaDTO?
    fun saveTechnologiaKolumny(numer: Int, k1: String?, k2: String?, k3: String?, k4: String?): Boolean
    fun updateFlag(numer: Int, flagType: FlagType, value: Boolean): Boolean
    fun initializeProduceFlags()
    fun getReportData(): List<ReportDTO>
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
    private val reportRepository: ReportRepository,
    private val mapper: ProbkaMapper,
    private val statusResolver: StatusResolver
) : ProbkaService {

    override fun getProbki(monthBack: Long): List<ProbkaDTO> {
        val probkiZO = repository.findProbkiZO(monthBack)

        return probkiZO.map { zo ->
            val zdList = repository.findZDByNumer(zo.numer)
            val zlList = repository.findZLByNumer(zo.numer)
            val zkList = repository.findZKByNumer(zo.numer)
            val technologia = repository.findTechnologiaNumer(zo.numer)
            //val kontrahent = repository.findKontrahentById(zo.idKontrahenta)

            mapper.toProbkaDTO(zo, zdList, zlList, zkList,  technologia, null, statusResolver)
        }
    }

    override fun getProbkaDetails(numer: Int): ProbkaDTO? {
        val probkiZO = repository.findProbkiZO()
        val zo = probkiZO.find {
            it.numer == numer
        } ?: return null

        val zdList = repository.findZDByNumer(numer)
        val zlList = repository.findZLByNumer(numer)
        val zkList = repository.findZKByNumer(numer)
        val technologia = repository.findTechnologiaNumer(numer)
        //val kontrahent = repository.findKontrahentById(zo.idKontrahenta)

        return mapper.toProbkaDTO(zo, zdList, zlList,zkList, technologia, null,statusResolver)
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
        val probkiZO = repository.findProbkiZO()

        probkiZO.forEach { zo ->
            val technologia = repository.findTechnologiaNumer(zo.numer)

            // Oblicz wartość produce z statusu
            val produceValue = when (zo.stan) {
                0.toByte() -> true
                1.toByte(), 2.toByte() -> false
                else -> null
            }

            // 3. Logika zapisu/aktualizacji
            if (technologia == null) {
                // 🆕 PUNKT 0: Utwórz nowy rekord TYLKO WTEDY, gdy próbka ma status
                // Planowane (2) lub Do Realizacji (1), co przekłada się na produceValue = false.
                // Jeśli produceValue == true (Zrealizowane) lub produceValue == null, ignorujemy.
                if (produceValue == false) {
                    repository.saveTechnologia(
                        Technologia(
                            numer = zo.numer,
                            produce = produceValue, // Zawsze false przy tworzeniu z null
                            send = null, // Flagi nieaktywne, bo nie zrealizowano
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
                repository.saveTechnologia(updated)
            }
        }
    }

    override fun getReportData(): List<ReportDTO> {
        return reportRepository.findOpenOrdersForTychy()
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