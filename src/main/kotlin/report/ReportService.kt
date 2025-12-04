package report

import java.time.format.DateTimeFormatter

interface ReportServiceI {
    fun przygotujDaneDoRaportu(filter: RaportFilter): List<ReportDTO>
}

class ReportService(
    private val reportRepository: ReportRepository,
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
) : ReportServiceI {
    override fun przygotujDaneDoRaportu(filter: RaportFilter): List<ReportDTO> {
        val daneZBazy = reportRepository.findReportData(filter)

        return daneZBazy.map { zo ->
            val technologia = zo.technologia
            val zd = zo.statusZD?.firstOrNull()
            val zk = zo.statusZK?.firstOrNull()

            val sortedZLL = zo.statusZL?.sortedBy { it.id }
            val zl1 = sortedZLL?.getOrNull(0)
            val zl2 = sortedZLL?.getOrNull(1)

            ReportDTO(
                numerZlecenia = zo.numer,
                kontrahent = zo.kontrahent?.nazwa ?: "Nieznany",
                nazwaProbki = zo.opis1 ?: "",

                statusZO = StatusConverter.mapToSymbol(zo.stan),
                terminZO = zo.terminZak?.format(dateFormatter) ?: "-",
                statusZD = StatusConverter.mapToSymbol(zd?.stan),
                statusZK = StatusConverter.mapToSymbol(zk?.stan),
                statusZL1 = StatusConverter.mapToSymbol(zl1?.stan),
                statusZL2 = StatusConverter.mapToSymbol(zl2?.stan),

                art = zo.art ?: "",
                receptura = zo.receptura1 ?: "",
                szerokosc = zo.szerokosc1?.toString() ?: "-",
                grubosc1 = zo.grubosc11 ?: "",
                grubosc2 = zo.grubosc21 ?: "",
                grubosc3 = zo.grubosc31 ?: "",

                opis = technologia?.opis ?: "",
                dodatkoweInfo = technologia?.dodatkoweInfo ?: ""
            )
        }.sortedBy { it.numerZlecenia }
    }
}