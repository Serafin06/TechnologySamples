package base

import dataBase.Kontrahent
import dataBase.Technologia
import dataBase.ZD
import dataBase.ZK
import dataBase.ZL
import dataBase.ZO
import org.hibernate.Session
import org.hibernate.SessionFactory
import report.RaportFilter
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// ═══════════════════════════════════════════════════════════════
// 🗃️ Repository Layer - Warstwa dostępu do danych
// ═══════════════════════════════════════════════════════════════

/**
 * Interface dla repository - łatwe mockowanie i testowanie
 */
interface ProbkaRepository {
    fun findProbkiZO(monthsBack: Long = 6): List<ZO>
    fun findZKByNumer(numer: Int): List<ZK>
    fun findZDByNumer(numer: Int): List<ZD>
    fun findZLByNumer(numer: Int): List<ZL>
    fun findTechnologiaNumer(numer: Int): Technologia?
    fun findTechnologiaByNumers(numers: Collection<Int>): Map<Int, Technologia>
    fun findAllTechnologia(): List<Technologia>
    fun findProbkiWithDetails(monthsBack: Long): List<ZO>
    fun findProbkaWithDetailsByNumer(numer: Int): ZO?
    fun saveTechnologia(technologia: Technologia): Technologia
    fun saveAllTechnologia(technologiaList: List<Technologia>)
    fun findKontrahentById(id: Int): Kontrahent?
    fun findKontrahenciByIds(ids: Collection<Int>): Map<Int, Kontrahent>
    fun findReportData(filter: RaportFilter): List<ZO>
    fun testConnection()
}

/**
 * Implementacja repository używająca Hibernate
 */
class ProbkaRepositoryImpl(private val sessionFactory: SessionFactory) : ProbkaRepository {

    override fun findProbkiZO(monthsBack: Long): List<ZO> {
        val dateFrom = LocalDateTime.now().minus(monthsBack, ChronoUnit.MONTHS)

        return useSession { session ->
            session.createQuery(
                "FROM ZO WHERE proba = 1 AND data >= :fromDate ORDER BY data DESC",
                ZO::class.java
            ).apply {
                setParameter("fromDate", dateFrom)
            }.list()
        }
    }

    override fun findZKByNumer(numer: Int): List<ZK> {
        return useSession { session ->
            session.createQuery(
                "FROM ZK WHERE numer = :numer",
                ZK::class.java
            ).apply {
                setParameter("numer", numer)
            }.list()
        }
    }

    override fun findZDByNumer(numer: Int): List<ZD> {
        return useSession { session ->
            session.createQuery(
                "FROM ZD WHERE numer = :numer",
                ZD::class.java
            ).apply {
                setParameter("numer", numer)
            }.list()
        }
    }

    override fun findZLByNumer(numer: Int): List<ZL> {
        return useSession { session ->
            session.createQuery(
                "FROM ZL WHERE numer = :numer",
                ZL::class.java
            ).apply {
                setParameter("numer", numer)

            }.list()
        }
    }

    override fun findTechnologiaNumer(numer: Int): Technologia? {
        return useSession { session ->
            session.createQuery(
                "FROM Technologia WHERE numer = :numer",
                Technologia::class.java
            ).apply {
                setParameter("numer", numer)
            }.uniqueResultOptional().orElse(null)
        }
    }

    override fun findTechnologiaByNumers(numers: Collection<Int>): Map<Int, Technologia> {
        if (numers.isEmpty()) return emptyMap()
        return useSession { session ->
            val list = session.createQuery(
                "FROM Technologia WHERE numer IN :numers",
                Technologia::class.java
            ).setParameter("numers", numers).resultList
            // Tworzymy mapę dla szybkiego dostępu O(1)
            list.associateBy { it.numer }
        }
    }

    override fun findAllTechnologia(): List<Technologia> {
        return useSession { session ->
            session.createQuery("FROM Technologia", Technologia::class.java).list()
        }
    }

    override fun findProbkiWithDetails(monthsBack: Long): List<ZO> {
        val dateFrom = LocalDateTime.now().minus(monthsBack, ChronoUnit.MONTHS)

        // 1. Główne zapytanie POZOSTAWIAMY, ale USUWAMY JOIN FETCH dla technologii
        val hql = StringBuilder()
        hql.append("SELECT zo FROM ZO zo ")
        hql.append("LEFT JOIN FETCH zo.statusZD ")
        hql.append("LEFT JOIN FETCH zo.statusZK ")
        hql.append("LEFT JOIN FETCH zo.statusZL ")
        // hql.append("LEFT JOIN FETCH zo.technologia ") // <--- TĘ LINIĘ USUWAMY
        hql.append("WHERE zo.proba = 1 AND zo.data >= :fromDate ")
        hql.append("ORDER BY zo.data DESC")

        return useSession { session ->
            val list = session.createQuery(hql.toString(), ZO::class.java).apply {
                setParameter("fromDate", dateFrom)
            }.resultList

            // 2. Usuwamy duplikaty (ważne przy JOIN FETCH do kolekcji)
            val distinctList = LinkedHashSet(list).toList()

            // 3. Batch load Kontrahenci (bez zmian)
            val kontrahentIds: Set<Int> = distinctList.mapNotNull { it.idKontrahenta }.toSet()
            val kontrahentMap: Map<Int, Kontrahent> = if (kontrahentIds.isNotEmpty()) {
                findKontrahenciByIds(kontrahentIds)
            } else {
                emptyMap()
            }

            // 4. NOWE: Batch load Technologia
            val numerList: Set<Int> = distinctList.map { it.numer }.toSet()
            val technologiaMap: Map<Int, Technologia> = if (numerList.isNotEmpty()) {
                findTechnologiaByNumers(numerList)
            } else {
                emptyMap()
            }

            // 5. Przypisujemy obiekty w pamięci
            distinctList.forEach { zo ->
                zo.kontrahent = kontrahentMap[zo.idKontrahenta]
                zo.technologia = technologiaMap[zo.numer]
            }

            distinctList
        }
    }

    override fun findProbkaWithDetailsByNumer(numer: Int): ZO? {
        val hql = StringBuilder()
        hql.append("SELECT zo FROM ZO zo ")
        hql.append("LEFT JOIN FETCH zo.statusZD ")
        hql.append("LEFT JOIN FETCH zo.statusZK ")
        hql.append("LEFT JOIN FETCH zo.statusZL ")
        hql.append("LEFT JOIN FETCH zo.technologia ")
        hql.append("LEFT JOIN FETCH zo.kontrahent ") // Dołączamy kontrahenta od razu
        hql.append("WHERE zo.numer = :numer")

        return useSession { session ->
            session.createQuery(hql.toString(), ZO::class.java)
                .setParameter("numer", numer)
                .resultList
                .firstOrNull()
        }
    }

    override fun saveTechnologia(technologia: Technologia): Technologia {
        return useSession { session ->
            val transaction = session.beginTransaction()
            try {
                session.merge(technologia).also {
                    transaction.commit()
                }
            } catch (e: Exception) {
                transaction.rollback()
                throw e
            }
        }
    }

    override fun saveAllTechnologia(technologiaList: List<Technologia>) {
        if (technologiaList.isEmpty()) return

        useSession { session ->
            val transaction = session.beginTransaction()
            try {
                // Używamy pętli w ramach jednej transakcji, co jest znacznie szybsze
                technologiaList.forEach { technologia ->
                    session.merge(technologia) // merge obsłuży zarówno nowe, jak i istniejące obiekty
                }
                transaction.commit()
            } catch (e: Exception) {
                transaction.rollback()
                throw e
            }
        }
    }

    override fun findKontrahentById(id: Int): Kontrahent? {
        val idBig = id.toBigDecimalId()
        return useSession { session ->
            session.createQuery(
                "FROM Kontrahent WHERE idKontrahenta = :id",
                Kontrahent ::class.java
            ).apply {
                setParameter("id",  idBig)
            }.uniqueResultOptional().orElse(null)
        }
    }

    override fun findKontrahenciByIds(ids: Collection<Int>): Map<Int, Kontrahent> {
        if (ids.isEmpty()) return emptyMap()
        val idsBig = ids.map { it.toBigDecimalId() }
        return useSession { session ->
            val list = session.createQuery(
                "FROM Kontrahent WHERE idKontrahenta IN :ids",
                Kontrahent::class.java
            ).setParameter("ids", idsBig).resultList
            list.associateBy { it.idKontrahenta.intValueExact() } // klucz Int
        }
    }

    override fun findReportData(filter: RaportFilter): List<ZO> {
        val dateFrom = LocalDateTime.now().minus(12, ChronoUnit.MONTHS)

        val hql = StringBuilder()
        hql.append("SELECT zo FROM ZO zo ")
        hql.append("LEFT JOIN FETCH zo.statusZD zd ")
        hql.append("LEFT JOIN FETCH zo.statusZK zk ")
        hql.append("LEFT JOIN FETCH zo.statusZL zl ")
        hql.append("LEFT JOIN FETCH zo.technologia t ")
        hql.append("WHERE zo.proba = 1 AND zo.data >= :fromDate ")

        if (filter.oddzialNazwa != null) {
            hql.append("AND zo.oddzialW = :oddzialWCode ")
        }
        if (filter.tylkoOtwarte) {
            hql.append("AND zo.stan IN (1, 2) ")
        }
        hql.append("ORDER BY zo.data DESC")

        return useSession { session ->
            val list = session.createQuery(hql.toString(), ZO::class.java).apply {
                setParameter("fromDate", dateFrom)
                if (filter.oddzialNazwa != null) {
                    val oddzialCode = when (filter.oddzialNazwa) {
                        "Tychy" -> 12.toByte()
                        "Ignatki" -> 11.toByte()
                        else -> throw IllegalArgumentException("Nieznana nazwa oddziału dla filtra: ${filter.oddzialNazwa}")
                    }
                    setParameter("oddzialWCode", oddzialCode)
                }
            }.resultList

            // --- BATCH: zbierz unikalne id kontrahentów (Int)
            val kontrahentIds: Set<Int> = list.mapNotNull { it.idKontrahenta }.toSet()

            // pobierz wszystkie kontrahenty jednym zapytaniem i zmapuj na Int -> Kontrahent
            val kontrahentMap: Map<Int, Kontrahent> = findKontrahenciByIds(kontrahentIds)

            // przypisz kontrahenta do każdego ZO (transient pole)
            list.forEach { zo ->
                zo.kontrahent = kontrahentMap[zo.idKontrahenta]
            }

            list
        }
    }


    override fun testConnection() {
        val session = sessionFactory.openSession()
        try {
            session.beginTransaction()
            session.createNativeQuery("SELECT 1").singleResult
            session.transaction.commit()
        } catch (ex: Exception) {
            session.transaction.rollback()
            throw ex
        } finally {
            session.close()
        }
    }

    private fun <T> useSession(block: (Session) -> T): T {
        val session = sessionFactory.openSession()
        return try {
            block(session)
        } finally {
            session.close()
        }
    }
}