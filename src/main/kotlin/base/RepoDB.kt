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
    fun findTechnologiaByNumers(numers: Collection<Int>): Map<Int, Technologia>
    fun findProbkiWithDetails(monthsBack: Long): List<ZO>
    fun findProbkiStanOnly(monthsBack: Long): Map<Int, Byte>
    fun batchInsertTechnologia(entities: List<Technologia>)
    fun batchUpdateTechnologia(entities: List<Technologia>)
    fun findKontrahenciByIds(ids: Collection<Int>): Map<Int, Kontrahent>
    fun testConnection()
    fun <T> useSession(block: (Session) -> T): T
}

/**
 * Implementacja repository używająca Hibernate
 */
class ProbkaRepositoryImpl(private val sessionFactory: SessionFactory) : ProbkaRepository {

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


    // Lżejsze zapytanie - tylko numer i stan
    override fun findProbkiStanOnly(monthsBack: Long): Map<Int, Byte> {
        val dateFrom = LocalDateTime.now().minus(monthsBack, ChronoUnit.MONTHS)

        return useSession { session ->
            session.createQuery(
                "SELECT zo.numer, zo.stan FROM ZO zo WHERE zo.proba = 1 AND zo.data >= :fromDate",
                Array<Any>::class.java
            ).apply {
                setParameter("fromDate", dateFrom)
            }.resultList.associate { it[0] as Int to it[1] as Byte }
        }
    }


    // Batch insert
    override fun batchInsertTechnologia(entities: List<Technologia>) {
        useSession { session ->
            val tx = session.beginTransaction()
            entities.forEachIndexed { index, entity ->
                session.persist(entity)
                if (index % 50 == 0) {
                    session.flush()
                    session.clear()
                }
            }
            tx.commit()
        }
    }

    // Batch update
    override fun batchUpdateTechnologia(entities: List<Technologia>) {
        useSession { session ->
            val tx = session.beginTransaction()
            entities.forEachIndexed { index, entity ->
                session.merge(entity)
                if (index % 50 == 0) {
                    session.flush()
                    session.clear()
                }
            }
            tx.commit()
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

    override fun <T> useSession(block: (Session) -> T): T {
        val session = sessionFactory.openSession()
        return try {
            block(session)
        } finally {
            session.close()
        }
    }
}