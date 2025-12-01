package report

/**
 * DTO przeznaczone do generowania wiersza w raporcie PDF/Excel.
 * Możesz to rozszerzyć o pola wyliczone, np. 'czyOtwarta'.
 */
data class ReportDTO(
    val numerZlecenia: Int,
    val kontrahent: String,
    val nazwaProbki: String,

    val statusZD: String,      // Już symbole: W, P, R, A
    val terminZO: String,
    val statusZK: String,

    val statusZL1: String,
    val statusZL2: String,

    val art: String,
    val receptura: String,
    val szerokosc: String,
    val grubosc1: String,
    val grubosc2: String,
    val grubosc3: String,

    val opis: String,
    val dodatkoweInfo: String
)

data class RaportFilter(
    val oddzialNazwa: String? = null,
    val tylkoOtwarte: Boolean = false, // True = statusy 1 (W realizacji) i 2 (Planowane)
    // Opcjonalnie: zakres dat, konkretny kontrahent itd.
) {
    companion object {
        // Gotowy preset dla Twojego pierwszego raportu
        fun tychyOtwarte(): RaportFilter {
            return RaportFilter(
                oddzialNazwa = "Tychy",
                tylkoOtwarte = true
            )
        }
    }
}