package report.magazyn

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

/** Generuje raport magazynu próbek do pliku PDF */
class MagazynPdfReportGenerator {

    fun generuj(dane: List<MagazynReportDTO>, sciezka: String): File {
        val file = File(sciezka)
        val document = Document(PageSize.A4.rotate())
        PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()

        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f)
        document.add(Paragraph("Magazyn próbek", titleFont).apply {
            alignment = Element.ALIGN_CENTER
            spacingAfter = 20f
        })

        val table = PdfPTable(8)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(6f, 18f, 14f, 12f, 10f, 8f, 16f, 12f))

        listOf("Numer", "Kontrahent", "Skład", "Struktura", "Szerokość", "Ilość", "Uwagi", "Data produkcji")
            .forEach { table.addCell(headerCell(it)) }

        dane.forEach { dto ->
            table.addCell(cell(dto.numer.toString()))
            table.addCell(cell(dto.kontrahent))
            table.addCell(cell(dto.sklad))
            table.addCell(cell(dto.struktura))
            table.addCell(cell(dto.szerokosc))
            table.addCell(cell(dto.ilosc))
            table.addCell(cell(dto.uwagi))
            table.addCell(cell(dto.dataProdukcji))
        }

        document.add(table)
        document.close()
        return file
    }

    private fun headerCell(text: String) = PdfPCell(
        Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f))
    ).apply {
        backgroundColor = BaseColor.LIGHT_GRAY
        horizontalAlignment = Element.ALIGN_CENTER
        setPadding(5f)
    }

    private fun cell(text: String) = PdfPCell(
        Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 8f))
    ).apply {
        setPadding(4f)
    }
}