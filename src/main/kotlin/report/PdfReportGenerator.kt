package report

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

class PdfReportGenerator : ReportGenerator {

    override fun generujReport(dane: List<ReportDTO>, sciezkaPliku: String): File {
        val file = File(sciezkaPliku)
        val document = Document(PageSize.A4.rotate()) // Landscape dla więcej kolumn

        PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()

        // Nagłówek dokumentu
        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f)
        val title = Paragraph("Próbki Technologiczne", titleFont)
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 20f
        document.add(title)

        // Tabela (16 kolumn)
        val table = PdfPTable(16)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(
            8f,  // Numer
            12f, // Kontrahent
            15f, // Nazwa Próbki
            10f, // Termin
            8f,  // Drukowanie
            8f,  // Laminacja 1
            8f,  // Laminacja 2
            8f,  // Krajarki
            8f,  // Art
            10f, // Receptura
            7f,  // Szerokość
            6f,  // Grubość 1
            6f,  // Grubość 2
            6f,  // Grubość 3
            12f, // Opis
            12f  // Info Dodatkowe
        ))

        // Nagłówki
        val headers = listOf(
            "Numer", "Kontrahent", "Nazwa Próbki",
            "Termin", "Drukowanie", "Laminacja 1", "Laminacja 2", "Krajarki",
            "Art", "Receptura", "Szerokość",
            "Grubość 1", "Grubość 2", "Grubość 3",
            "Opis", "Info Dodatkowe"
        )

        headers.forEach { header ->
            table.addCell(createHeaderCell(header))
        }

        // Dane
        dane.forEach { dto ->
            // Identyfikacja
            table.addCell(createCell(dto.numerZlecenia.toString()))
            table.addCell(createCell(dto.kontrahent))
            table.addCell(createCell(dto.nazwaProbki))

            // Termin
            table.addCell(createCell(dto.terminZO))

            // Statusy z kolorami
            table.addCell(createStatusCell(dto.statusZD))
            table.addCell(createStatusCell(dto.statusZL1))
            table.addCell(createStatusCell(dto.statusZL2))
            table.addCell(createStatusCell(dto.statusZK))

            // Dane techniczne
            table.addCell(createCell(dto.art))
            table.addCell(createCell(dto.receptura))
            table.addCell(createCell(dto.szerokosc))
            table.addCell(createCell(dto.grubosc1))
            table.addCell(createCell(dto.grubosc2))
            table.addCell(createCell(dto.grubosc3))

            // Informacje
            table.addCell(createCell(dto.opis))
            table.addCell(createCell(dto.dodatkoweInfo))
        }

        document.add(table)
        document.close()

        return file
    }

    private fun createHeaderCell(text: String): PdfPCell {
        val font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f)
        val cell = PdfPCell(Phrase(text, font))
        cell.backgroundColor = BaseColor.LIGHT_GRAY
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        cell.setPadding(5f)
        return cell
    }

    private fun createCell(text: String): PdfPCell {
        val font = FontFactory.getFont(FontFactory.HELVETICA, 8f)
        val cell = PdfPCell(Phrase(text, font))
        cell.horizontalAlignment = Element.ALIGN_LEFT
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        cell.setPadding(4f)
        return cell
    }

    private fun createStatusCell(status: String): PdfPCell {
        val symbol = StatusExtractor.extractSymbol(status)
        val displayText = if (symbol.isEmpty()) "-" else symbol

        val font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f)
        val cell = PdfPCell(Phrase(displayText, font))

        // Kolor tła według symbolu
        cell.backgroundColor = when (symbol) {
            "W" -> BaseColor.WHITE
            "R" -> BaseColor(144, 238, 144) // Light green
            "P" -> BaseColor(255, 255, 153) // Light yellow
            "A" -> BaseColor(255, 102, 102) // Light red
            else -> BaseColor.LIGHT_GRAY
        }

        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        cell.setPadding(4f)
        return cell
    }
}