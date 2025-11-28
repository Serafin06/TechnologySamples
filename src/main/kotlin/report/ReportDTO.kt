package report

import base.ProbkaDTO

/**
 * DTO przeznaczone do generowania wiersza w raporcie PDF/Excel.
 * Możesz to rozszerzyć o pola wyliczone, np. 'czyOtwarta'.
 */
data class ReportDTO(
    // --- Identyfikacja ---
    val numerZlecenia: Int,
    val kontrahent: String,
    val nazwaProbki: String, // z pola 'nazwa' (opis1)

    // --- Statusy (Poszczególne etapy) ---
    val statusZO: String,      // Główny status
    val terminZO: String,      // Data terminu (sformatowana stringiem)

    val statusZD: String,      // Druk

    // Rozbite laminacje (skoro są max 2)
    val statusZL1: String,
    val statusZL2: String,

    // --- Dane Techniczne (Struktura) ---
    val art: String,
    val receptura: String,
    val szerokosc: String,     // Jako string, żeby łatwo obsłużyć null np. "-"
    val grubosc1: String,      // grubosc11
    val grubosc2: String,      // grubosc21
    val grubosc3: String,      // grubosc31

    // --- Informacje Dodatkowe ---
    val opis: String,          // z Technologia.opis
    val dodatkoweInfo: String  // z Technologia.dodatkoweInfo
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