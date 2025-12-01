package report


import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.getOrPut

class ReportExcelGenerator : ReportGenerator {

    override fun generujReport(dane: List<ReportDTO>, sciezkaDocelowa: String): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Raport Produkcji")

        // Style cache
        val headerStyle = createHeaderStyle(workbook)
        val colorStyles = mutableMapOf<String, XSSFCellStyle>()

        // Nagłówki
        val headers = listOf(
            "Numer", "Kontrahent", "Nazwa Próbki",
            "Drukowanie", "Termin", "Krajarki",
            "Laminacja 1", "Laminacja 2",
            "Art", "Receptura", "Szerokość",
            "Grubość 1", "Grubość 2", "Grubość 3",
            "Opis", "Info Dodatkowe"
        )

        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, title ->
            headerRow.createCell(index).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        // Dane
        dane.forEachIndexed { idx, dto ->
            val row = sheet.createRow(idx + 1)

            row.createCell(0).setCellValue(dto.numerZlecenia.toDouble())
            row.createCell(1).setCellValue(dto.kontrahent)
            row.createCell(2).setCellValue(dto.nazwaProbki)

            // Statusy z kolorami
            createStatusCell(row, 3, dto.statusZD, workbook, colorStyles)
            row.createCell(4).setCellValue(dto.terminZO)
            createStatusCell(row, 5, dto.statusZK, workbook, colorStyles)
            createStatusCell(row, 6, dto.statusZL1, workbook, colorStyles)
            createStatusCell(row, 7, dto.statusZL2, workbook, colorStyles)

            row.createCell(8).setCellValue(dto.art)
            row.createCell(9).setCellValue(dto.receptura)
            row.createCell(10).setCellValue(dto.szerokosc)
            row.createCell(11).setCellValue(dto.grubosc1)
            row.createCell(12).setCellValue(dto.grubosc2)
            row.createCell(13).setCellValue(dto.grubosc3)
            row.createCell(14).setCellValue(dto.opis)
            row.createCell(15).setCellValue(dto.dodatkoweInfo)
        }

        // Autodopasowanie
        headers.indices.forEach { sheet.autoSizeColumn(it) }

        // Zapis
        val file = File(sciezkaDocelowa)
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()

        return file
    }

    private fun createHeaderStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        return workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }
    }

    private fun createStatusCell(
        row: Row,
        index: Int,
        symbol: String,
        workbook: XSSFWorkbook,
        styleCache: MutableMap<String, XSSFCellStyle>
    ) {
        val style = styleCache.getOrPut(symbol) {
            workbook.createCellStyle().apply {
                val color = StatusConverter.mapToColor(symbol)
                fillForegroundColor = color.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
            }
        }

        row.createCell(index).apply {
            setCellValue(symbol)
            cellStyle = style
        }
    }
}