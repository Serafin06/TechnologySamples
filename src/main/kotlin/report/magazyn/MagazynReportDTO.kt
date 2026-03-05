package report.magazyn

data class MagazynReportDTO(
    val numer: Int,
    val kontrahent: String,
    val sklad: String,
    val struktura: String,
    val szerokosc: String,
    val ilosc: String,
    val uwagi: String,
    val dataProdukcji: String
)

data class MagazynRaportFilter(
    val kontrahent: String? = null,
    val sklad: String? = null,
    val szerokosc: String? = null
)