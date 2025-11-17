package pl.rafapp.techSam.Base

import pl.rafapp.techSam.DataBase.*
import org.hibernate.Session
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

// ═══════════════════════════════════════════════════════════════
// 🗃️ Repository Layer - Warstwa dostępu do danych
// ═══════════════════════════════════════════════════════════════

/**
 * Interface dla repository - łatwe mockowanie i testowanie
 */
interface ProbkaRepository {
    fun findProbkiZO(): List<ZO>
    fun findZKByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZK>
    fun findZDByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZD>
    fun findZLByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZL>
    fun findTechnologiaNumer(numer: Int): Technologia?
    fun saveTechnologia(technologia: Technologia): Technologia
}

/**
 * Implementacja repository używająca Hibernate
 */
class ProbkaRepositoryImpl(private val sessionFactory: org.hibernate.SessionFactory) : ProbkaRepository {

    override fun findProbkiZO(): List<ZO> {
        val sixMonthsAgo = LocalDateTime.now().minus(6, ChronoUnit.MONTHS)

        return useSession { session ->
            session.createQuery(
                "FROM ZO WHERE proba = 1 AND data >= :fromDate ORDER BY data DESC",
                ZO::class.java
            ).apply {
                setParameter("fromDate", sixMonthsAgo)
            }.list()
        }
    }

    override fun findZKByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZK> {
        return useSession { session ->
            session.createQuery(
                "FROM ZK WHERE numer = :numer AND oddzial = :oddzial AND rok = :rok",
                ZK::class.java
            ).apply {
                setParameter("numer", numer)
                setParameter("oddzial", oddzial)
                setParameter("rok", rok)
            }.list()
        }
    }

    override fun findZDByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZD> {
        return useSession { session ->
            session.createQuery(
                "FROM ZD WHERE numer = :numer AND oddzial = :oddzial AND rok = :rok",
                ZD::class.java
            ).apply {
                setParameter("numer", numer)
                setParameter("oddzial", oddzial)
                setParameter("rok", rok)
            }.list()
        }
    }

    override fun findZLByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZL> {
        return useSession { session ->
            session.createQuery(
                "FROM ZL WHERE numer = :numer AND oddzial = :oddzial AND rok = :rok",
                ZL::class.java
            ).apply {
                setParameter("numer", numer)
                setParameter("oddzial", oddzial)
                setParameter("rok", rok)
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

    private fun <T> useSession(block: (Session) -> T): T {
        val session = sessionFactory.openSession()
        return try {
            block(session)
        } finally {
            session.close()
        }
    }
}