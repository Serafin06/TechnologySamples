package base

import dataBase.Technologia
import dataBase.ZD
import dataBase.ZK
import dataBase.ZL
import dataBase.ZO
import org.hibernate.Session
import org.hibernate.SessionFactory
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
    fun saveTechnologia(technologia: Technologia): Technologia
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