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
import java.math.BigDecimal
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
    fun findKontrahentById(id: Int): Kontrahent?
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

    override fun findReportData(filter: RaportFilter): List<ZO> {
        // Domyślny zakres 12 miesięcy, tak jak w oryginalnym kodzie
        val dateFrom = LocalDateTime.now().minus(12, ChronoUnit.MONTHS)

        val hql = StringBuilder()
        hql.append("SELECT zo FROM ZO zo ")
        // Teraz te pola istnieją w Encji ZO i są ładowane jednym zapytaniem
        hql.append("LEFT JOIN FETCH zo.statusZD zd ")
        hql.append("LEFT JOIN FETCH zo.statusZK zk ")
        hql.append("LEFT JOIN FETCH zo.statusZL zl ")
        hql.append("LEFT JOIN FETCH zo.technologia t ") // Używamy 'technologia'
        hql.append("WHERE zo.proba = 1 AND zo.data >= :fromDate ")

        // Dodajemy filtry bezpośrednio do HQL (filtr Oddziału)
        if (filter.oddzialNazwa != null) {
            hql.append("AND zo.oddzialW = :oddzialWCode ")
        }

        // Dodajemy filtr 'tylkoOtwarte' (statusy 1 i 2)
        if (filter.tylkoOtwarte) {
            hql.append("AND zo.stan IN (1, 2) ")
        }

        hql.append("ORDER BY zo.data DESC")

        return useSession { session ->
            session.createQuery(hql.toString(), ZO::class.java).apply {
                setParameter("fromDate", dateFrom)
                if (filter.oddzialNazwa != null) {
                    // 💡 MUSIMY PRZETŁUMACZYĆ NAZWĘ NA KOD LICZBOWY
                    val oddzialCode = when (filter.oddzialNazwa) {
                        "Tychy" -> 12.toByte()
                        "Ignatki" -> 11.toByte()
                        else -> throw IllegalArgumentException("Nieznana nazwa oddziału dla filtra: ${filter.oddzialNazwa}")
                    }
                    setParameter("oddzialWCode", oddzialCode)

                }
                // Hibernate/JPA automatycznie obsłuży dociągnięte kolekcje (ZK, ZL)
            }.list()
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