package report

import base.ProbkaRepository
import dataBase.ZO
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface ReportRepositoryI {
    fun findReportData(filter: RaportFilter): List<ZO>
}

class ReportRepository(
    private val probkaRepository: ProbkaRepository
) : ReportRepositoryI {

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

        return probkaRepository.useSession { session ->
            val list = session.createQuery(hql.toString(), ZO::class.java).apply {
                setParameter("fromDate", dateFrom)
                if (filter.oddzialNazwa != null) {
                    val oddzialCode = when (filter.oddzialNazwa) {
                        "Tychy" -> 12.toByte()
                        "Ignatki" -> 11.toByte()
                        else -> throw IllegalArgumentException("Nieznana nazwa oddziału: ${filter.oddzialNazwa}")
                    }
                    setParameter("oddzialWCode", oddzialCode)
                }
            }.resultList

            val kontrahentIds: Set<Int> = list.mapNotNull { it.idKontrahenta }.toSet()
            val kontrahentMap = probkaRepository.findKontrahenciByIds(kontrahentIds)

            list.forEach { zo ->
                zo.kontrahent = kontrahentMap[zo.idKontrahenta]
            }

            list
        }
    }
}