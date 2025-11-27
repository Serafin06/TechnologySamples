package base

import dataBase.Technologia

/**
 * Interface serwisu - Single Responsibility Principle
 */
interface ProbkaService {
    fun getProbki(monthBack: Long = 6): List<ProbkaDTO>
    fun getProbkaDetails(numer: Int): ProbkaDTO?
    fun saveTechnologiaKolumny(numer: Int, k1: String?, k2: String?, k3: String?, k4: String?): Boolean
    fun testConnection(): Boolean
}

/**
 * Implementacja serwisu - logika biznesowa
 */
class ProbkaServiceImpl(
    private val repository: ProbkaRepository,
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

            mapper.toProbkaDTO(zo, zdList, zlList, zkList,  technologia, statusResolver)
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

        return mapper.toProbkaDTO(zo, zdList, zlList,zkList, technologia, statusResolver)
    }

    override fun saveTechnologiaKolumny(
        numer: Int,
        k1: String?,
        k2: String?,
        k3: String?,
        k4: String?
    ): Boolean {
        return try {
            val existing = repository.findTechnologiaNumer(numer)

            val technologia = existing?.copy(
                opis = k1,
                dodatkoweInfo = k2,
                uwagi = k3,
                testy = k4
            ) ?: Technologia(
                numer = numer,
                opis = k1,
                dodatkoweInfo = k2,
                uwagi = k3,
                testy = k4
            )

            repository.saveTechnologia(technologia)
            true
        } catch (e: Exception) {
            false
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