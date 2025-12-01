package report


import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class ExcelReportGenerator : ReportGenerator {

    override fun generujReport(dane: List<ReportDTO>, sciezkaDocelowa: String): File {
        // 1. Tworzymy skoroszyt (Workbook)
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Raport Produkcji")

        // 2. Definicja nagłówków
        val headers = listOf(
            "Numer", "Kontrahent", "Nazwa Próbki",
            "Termin", "Drukowanie", "Krajarki",
            "Laminacja 1", "Laminacja 2",
            "Art", "Receptura", "Szerokość",
            "Grubość 1", "Grubość 2", "Grubość 3",
            "Opis", "Info Dodatkowe"
        )

        // 3. Tworzymy styl dla nagłówka (pogrubienie)
        val headerStyle = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        headerStyle.setFont(font)

        // 4. Tworzymy wiersz nagłówkowy
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, title ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(title)
            cell.cellStyle = headerStyle
        }

        val statusStyleCache = mutableMapOf<String, org.apache.poi.ss.usermodel.CellStyle>()

        fun getStatusStyle(symbol: String): org.apache.poi.ss.usermodel.CellStyle {
            statusStyleCache[symbol]?.let { return it }
            val style = workbook.createCellStyle()
            val color = StatusConverter.mapToColor(symbol)
            style.fillForegroundColor = color.index
            style.fillPattern = FillPatternType.SOLID_FOREGROUND
            // opcjonalnie: obramowanie / wyrównanie
            style.alignment = org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER
            statusStyleCache[symbol] = style
            return style
        }

        // 5. Wypełniamy danymi
        var rowIdx = 1
        for (dto in dane) {
            val row = sheet.createRow(rowIdx++)

            // Wpisujemy dane w kolejności nagłówków
            row.createCell(0).setCellValue(dto.numerZlecenia.toDouble()) // Jako liczba
            row.createCell(1).setCellValue(dto.kontrahent)
            row.createCell(2).setCellValue(dto.nazwaProbki)

            row.createCell(3).setCellValue(dto.statusZO)
            row.createCell(4).setCellValue(dto.terminZO)
            row.createCell(5).setCellValue(dto.statusZD)

            row.createCell(6).setCellValue(dto.statusZL1)
            row.createCell(7).setCellValue(dto.statusZL2)

            row.createCell(8).setCellValue(dto.art)
            row.createCell(9).setCellValue(dto.receptura)
            row.createCell(10).setCellValue(dto.szerokosc) // String (np. "1200" lub "-")

            row.createCell(11).setCellValue(dto.grubosc1)
            row.createCell(12).setCellValue(dto.grubosc2)
            row.createCell(13).setCellValue(dto.grubosc3)

            row.createCell(14).setCellValue(dto.opis)
            row.createCell(15).setCellValue(dto.dodatkoweInfo)
        }

        // 6. Autodopasowanie szerokości kolumn (dla estetyki)
        for (i in headers.indices) {
            sheet.autoSizeColumn(i)
        }

        // 7. Zapis do pliku
        val file = File(sciezkaDocelowa)
        FileOutputStream(file).use { outputStream ->
            workbook.write(outputStream)
        }
        workbook.close()

        return file
    }
}