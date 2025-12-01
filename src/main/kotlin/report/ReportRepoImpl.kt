package report

import org.hibernate.SessionFactory
import java.time.LocalDateTime

class ReportRepoImpl(private val sf: SessionFactory) : ReportRepository {

    override fun findOpenOrdersForTychy(): List<ReportDTO> =
        sf.openSession().use { s ->
            val results = s.createQuery(
                """
                SELECT 
                    zo.numer,
                    COALESCE(zo.opis1, ''),
                    zo.terminZak,
                    zd.stan,
                    zk.stan,
                    zo.art,
                    zo.receptura1,
                    zo.szerokosc1,
                    zo.grubosc11,
                    zo.grubosc21,
                    zo.grubosc31,
                    t.opis,
                    t.dodatkoweInfo
                FROM ZO zo
                LEFT JOIN ZD zd ON zd.numer = zo.numer
                LEFT JOIN ZK zk ON zk.numer = zo.numer
                LEFT JOIN Technologia t ON t.numer = zo.numer
                WHERE zo.oddzialW = 12
                  AND zo.stan IN (1, 2)
                ORDER BY zo.data DESC
                """.trimIndent()
            ).list() as List<Array<Any?>>

            // Grupowanie po numerze ZO
            val grouped = results.groupBy { (it[0] as Int) }

            grouped.map { (numer, rows) ->
                val first = rows[0]

                // Pobranie statusów ZL
                val zlStatuses = sf.openSession().use { session ->
                    session.createQuery(
                        "SELECT zl.stan FROM ZL zl WHERE zl.numer = :numer ORDER BY zl.id",
                        Byte::class.java
                    ).setParameter("numer", numer)
                        .list()
                }

                ReportDTO(
                    numerZlecenia = numer,
                    kontrahent = "Nieznany",   // 👈 NA SZTYWNO — BO NIE MA KONTRAHENTA
                    nazwaProbki = first[1] as String,
                    statusZD = StatusConverter.mapToSymbol(first[3] as Byte?),
                    terminZO = (first[2] as LocalDateTime?)?.toString()?.take(10) ?: "",
                    statusZK = StatusConverter.mapToSymbol(first[4] as Byte?),
                    statusZL1 = StatusConverter.mapToSymbol(zlStatuses.getOrNull(0)),
                    statusZL2 = StatusConverter.mapToSymbol(zlStatuses.getOrNull(1)),
                    art = first[5] as String? ?: "",
                    receptura = first[6] as String? ?: "",
                    szerokosc = (first[7] as Short?)?.toString() ?: "",
                    grubosc1 = first[8] as String? ?: "",
                    grubosc2 = first[9] as String? ?: "",
                    grubosc3 = first[10] as String? ?: "",
                    opis = first[11] as String? ?: "",
                    dodatkoweInfo = first[12] as String? ?: ""
                )
            }
        }
}