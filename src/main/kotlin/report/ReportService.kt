package report

import base.ProbkaService

class ReportService(
    private val probkaService: ProbkaService,
) {
    fun przygotujDaneDoRaportu(filter: RaportFilter): List<ReportDTO> {
        return probkaService.getDaneDoRaportu(filter)
            .sortedBy { it.numerZlecenia } // Sortowanie pozostaje na warstwie Serwisu
    }
}