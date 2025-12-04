package report

import base.ProbkaRepository

// Funkcja fabrykująca dla serwisu raportów
fun createReportService(probkaRepository: ProbkaRepository): ReportServiceI {
    val reportRepository = ReportRepository(probkaRepository)
    return ReportService(reportRepository)
}