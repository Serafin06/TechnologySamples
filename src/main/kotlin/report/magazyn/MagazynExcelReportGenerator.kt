package report.magazyn

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import report.magazyn.MagazynReportDTO
import java.io.File
import java.io.FileOutputStream

/** Generuje raport magazynu próbek do pliku Excel */
class MagazynExcelReportGenerator {

    fun generuj(dane: List<MagazynReportDTO>, sciezka: String): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Magazyn próbek")

        val headers = listOf("Numer", "Kontrahent", "Skład", "Struktura", "Szerokość", "Ilość", "Uwagi", "Data produkcji")

        val headerStyle = workbook.createCellStyle()
        val font = workbook.createFont()
        font.bold = true
        headerStyle.setFont(font)

        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { i, title ->
            headerRow.createCell(i).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        dane.forEachIndexed { idx, dto ->
            val row = sheet.createRow(idx + 1)
            row.createCell(0).setCellValue(dto.numer.toDouble())
            row.createCell(1).setCellValue(dto.kontrahent)
            row.createCell(2).setCellValue(dto.sklad)
            row.createCell(3).setCellValue(dto.struktura)
            row.createCell(4).setCellValue(dto.szerokosc)
            row.createCell(5).setCellValue(dto.ilosc)
            row.createCell(6).setCellValue(dto.uwagi)
            row.createCell(7).setCellValue(dto.dataProdukcji)
        }

        for (i in headers.indices) sheet.autoSizeColumn(i)

        val file = File(sciezka)
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
        return file
    }
}