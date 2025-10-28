package pl.rafapp.techSam.Base




// 🏭 Service Layer - Logika biznesowa

/**
 * Interface dla serwisu - Single Responsibility Principle
 */
interface ProbkaService {
    fun getProbki(): List<ProbkaDTO>
    fun getProbkaDetails(numer: Int, oddzial: Byte, rok: Byte): ProbkaDTO?
}

/**
 * Implementacja serwisu - mapowanie danych i logika biznesowa
 */
class ProbkaServiceImpl(
    private val repository: ProbkaRepository,
    private val mapper: ProbkaMapper,
    private val statusResolver: StatusResolver
) : ProbkaService {

    override fun getProbki(): List<ProbkaDTO> {
        val probkiZO = repository.findProbkiZO()

        return probkiZO.map { zo ->
            val zkList = repository.findZKByNumer(zo.numer, zo.oddzial, zo.rok.toByte())
            val zdList = repository.findZDByNumer(zo.numer, zo.oddzial, zo.rok.toByte())

            mapper.toProbkaDTO(zo, zkList, zdList, statusResolver)
        }
    }

    override fun getProbkaDetails(numer: Int, oddzial: Byte, rok: Byte): ProbkaDTO? {
        val probkiZO = repository.findProbkiZO()
        val zo = probkiZO.find { it.numer == numer && it.oddzial == oddzial && it.rok == rok.toInt() }
            ?: return null

        val zkList = repository.findZKByNumer(numer, oddzial, rok)
        val zdList = repository.findZDByNumer(numer, oddzial, rok)

        return mapper.toProbkaDTO(zo, zkList, zdList, statusResolver)
    }
}