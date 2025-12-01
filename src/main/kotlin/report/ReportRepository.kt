package report

interface ReportRepository {
    fun findOpenOrdersForTychy(): List<ReportDTO>
}