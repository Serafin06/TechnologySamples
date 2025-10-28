package pl.rafapp.techSam.Base

import pl.rafapp.techSam.DataBase.*
import org.hibernate.Session

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
}

/**
 * Implementacja repository używająca Hibernate
 */
class ProbkaRepositoryImpl(private val sessionFactory: org.hibernate.SessionFactory) : ProbkaRepository {

    override fun findProbkiZO(): List<ZO> {
        return useSession { session ->
            session.createQuery(
                "FROM ZO WHERE proba = 1 ORDER BY data DESC",
                ZO::class.java
            ).list()
        }
    }

    override fun findZKByNumer(numer: Int, oddzial: Byte, rok: Byte): List<ZK> {
        return useSession { session ->
            session.createQuery(
                "FROM ZK WHERE NUMER = :numer AND ODDZIAL = :oddzial AND ROK = :rok",
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
                "FROM ZD WHERE NUMER = :numer AND ODDZIAL = :oddzial AND ROK = :rok",
                ZD::class.java
            ).apply {
                setParameter("numer", numer)
                setParameter("oddzial", oddzial)
                setParameter("rok", rok)
            }.list()
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