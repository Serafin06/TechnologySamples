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