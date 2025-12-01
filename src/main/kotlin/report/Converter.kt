package report

import org.apache.poi.ss.usermodel.IndexedColors

object StatusConverter {

    fun mapToSymbol(stan: Byte?): String =
        when (stan) {
            0.toByte() -> "W"
            1.toByte() -> "R"
            2.toByte() -> "P"
            4.toByte() -> "A"
            else -> ""
        }

    fun mapToColor(stan: String): IndexedColors =
        when (stan) {
            "W" -> IndexedColors.WHITE
            "R" -> IndexedColors.LIGHT_GREEN
            "P" -> IndexedColors.LIGHT_YELLOW
            "A" -> IndexedColors.RED
            else -> IndexedColors.GREY_25_PERCENT
        }
}