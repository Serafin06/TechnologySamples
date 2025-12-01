package report

import org.apache.poi.ss.usermodel.IndexedColors


object StatusConverter {

    // Byte? -> symbol ("W","R","P","A" lub "")
    fun mapToSymbol(stan: Byte?): String =
        when (stan) {
            0.toByte() -> "W"  // Wykonane
            1.toByte() -> "R"  // W realizacji
            2.toByte() -> "P"  // Planowane
            4.toByte() -> "A"  // Anulowane
            else -> ""
        }

    // symbol -> kolor tła (IndexedColors)
    fun mapToColor(symbol: String): IndexedColors =
        when (symbol) {
            "W" -> IndexedColors.WHITE
            "P" -> IndexedColors.LIGHT_YELLOW
            "R" -> IndexedColors.LIGHT_GREEN
            "A" -> IndexedColors.RED
            else -> IndexedColors.GREY_25_PERCENT
        }
}


object StatusExtractor {
    /**
     * Zwraca symbol: "W","P","R","A" lub "" (jeśli nie rozpoznano).
     * Przyjmuje dowolny tekst (np. "Wykonane", "W", "Planowane", "W realizacji", "Anulowane", "0 - Wykonane" itp.)
     */
    fun extractSymbol(statusText: String?): String {
        if (statusText == null) return ""

        val s = statusText.trim().lowercase()

        // Jeśli już jest pojedynczy symbol
        if (s == "w" || s == "p" || s == "r" || s == "a") return s.uppercase()

        return when {
            "wykon" in s -> "W"          // wykonane, wykonany
            "zakończ" in s || "zakoncz" in s -> "W"
            "plan" in s -> "P"           // planowane, plan
            "realiz" in s -> "R"         // w realizacji, realizacja
            "anul" in s -> "A"           // anulowane, anulacja
            // proste dopasowanie kodów liczbowych w tekście
            Regex("""\b0\b""").containsMatchIn(s) -> "W"
            Regex("""\b1\b""").containsMatchIn(s) -> "R"
            Regex("""\b2\b""").containsMatchIn(s) -> "P"
            Regex("""\b4\b""").containsMatchIn(s) -> "A"
            else -> ""
        }
    }
}
