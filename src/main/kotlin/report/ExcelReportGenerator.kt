package report


import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class ExcelReportGenerator : ReportGenerator {

    override fun generujReport(dane: List<ReportDTO>, sciezkaDocelowa: String): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Raport Produkcji")

        // Nagłówki w wymaganej kolejności: Druk / Laminacja1 / Laminacja2 / Krajarki
        val headers = listOf(
            "Numer", "Kontrahent", "Nazwa Próbki",
            "Termin", "Drukowanie", "Laminacja 1", "Laminacja 2", "Krajarki",
            "Art", "Receptura", "Szerokość",
            "Grubość 1", "Grubość 2", "Grubość 3",
            "Opis", "Info Dodatkowe"
        )

        // Styl nagłówka
        val headerStyle = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        headerStyle.setFont(font)

        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, title ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(title)
            cell.cellStyle = headerStyle
        }

        // Cache stylów statusów (symbol -> CellStyle)
        val statusStyleCache = mutableMapOf<String, org.apache.poi.ss.usermodel.CellStyle>()

        fun getStatusStyle(symbol: String): org.apache.poi.ss.usermodel.CellStyle {
            statusStyleCache[symbol]?.let { return it }
            val style = workbook.createCellStyle()
            val color = StatusConverter.mapToColor(symbol)
            style.fillForegroundColor = color.index
            style.fillPattern = FillPatternType.SOLID_FOREGROUND
            style.alignment = org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER
            statusStyleCache[symbol] = style
            return style
        }

        var rowIdx = 1
        for (dto in dane) {
            val row = sheet.createRow(rowIdx++)

            // Identyfikacja
            row.createCell(0).setCellValue(dto.numerZlecenia.toDouble())
            row.createCell(1).setCellValue(dto.kontrahent)
            row.createCell(2).setCellValue(dto.nazwaProbki)

            // Termin
            row.createCell(3).setCellValue(dto.terminZO)

            // 1) Drukowanie -> symbol i kolor (z pola statusZD)
            val symDruk = StatusExtractor.extractSymbol(dto.statusZD)
            val cellDruk = row.createCell(4)
            cellDruk.setCellValue(if (symDruk.isEmpty()) "-" else symDruk)
            cellDruk.cellStyle = getStatusStyle(symDruk) as XSSFCellStyle?

            // 2) Laminacja 1 -> symbol i kolor (z pola statusZL1)
            val symL1 = StatusExtractor.extractSymbol(dto.statusZL1)
            val cellL1 = row.createCell(5)
            cellL1.setCellValue(if (symL1.isEmpty()) "-" else symL1)
            cellL1.cellStyle = getStatusStyle(symL1) as XSSFCellStyle?

            // 3) Laminacja 2 -> symbol i kolor (z pola statusZL2)
            val symL2 = StatusExtractor.extractSymbol(dto.statusZL2)
            val cellL2 = row.createCell(6)
            cellL2.setCellValue(if (symL2.isEmpty()) "-" else symL2)
            cellL2.cellStyle = getStatusStyle(symL2) as XSSFCellStyle?

            // 4) Krajarki -> symbol i kolor (z pola statusZK)
            val symZK = StatusExtractor.extractSymbol(dto.statusZK)
            val cellZK = row.createCell(7)
            cellZK.setCellValue(if (symZK.isEmpty()) "-" else symZK)
            cellZK.cellStyle = getStatusStyle(symZK) as XSSFCellStyle?

            // Pozostałe pola techniczne i info
            row.createCell(8).setCellValue(dto.art)
            row.createCell(9).setCellValue(dto.receptura)
            row.createCell(10).setCellValue(dto.szerokosc)

            row.createCell(11).setCellValue(dto.grubosc1)
            row.createCell(12).setCellValue(dto.grubosc2)
            row.createCell(13).setCellValue(dto.grubosc3)

            row.createCell(14).setCellValue(dto.opis)
            row.createCell(15).setCellValue(dto.dodatkoweInfo)
        }

        // Auto-size kolumn
        for (i in headers.indices) sheet.autoSizeColumn(i)

        // Zapis pliku
        val file = File(sciezkaDocelowa)
        FileOutputStream(file).use { outputStream ->
            workbook.write(outputStream)
        }
        workbook.close()
        return file
    }
}