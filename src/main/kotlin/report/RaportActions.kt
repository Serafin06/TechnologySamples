package report

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

enum class ExportType { EXCEL, PDF }

fun generujRaportAkcja(
    scope: CoroutineScope,
    type: ExportType,
    reportService: ReportServiceI,
    onComplete: (success: Boolean, path: String) -> Unit
) {
    scope.launch(Dispatchers.Swing) { // Uruchamiamy w wątku Swing/UI
        val fileChooser = JFileChooser()

        // Ustawienie domyślnej nazwy pliku i filtra
        val extension = if (type == ExportType.EXCEL) "xlsx" else "pdf"
        fileChooser.selectedFile = File("Raport_Tychy_Otwarte.$extension")
        fileChooser.fileFilter = FileNameExtensionFilter("Pliki $type (*.$extension)", extension)

        val result = fileChooser.showSaveDialog(null) // Otwiera okno dialogowe zapisu

        if (result == JFileChooser.APPROVE_OPTION) {
            val sciezkaDocelowa = fileChooser.selectedFile.absolutePath
            val finalPath = if (!sciezkaDocelowa.endsWith(".$extension")) {
                "$sciezkaDocelowa.$extension" // Dodaj rozszerzenie, jeśli użytkownik pominął
            } else {
                sciezkaDocelowa
            }

            // Uruchomienie ciężkiej operacji w tle (Dispatchers.IO)
            launch {
                withContext(Dispatchers.IO) {
                    println("Rozpoczynanie generowania raportu do: $finalPath")

                    val generator: ReportGenerator = when(type) {
                        ExportType.EXCEL -> ExcelReportGenerator()
                        ExportType.PDF -> PdfReportGenerator()
                    }

                    try {
                        val filtr = RaportFilter.tychyOtwarte()
                        val dane = reportService.przygotujDaneDoRaportu(filtr)
                        generator.generujReport(dane, finalPath)
                        println("Sukces! Raport zapisany.")

                        // 💡 WYWOŁANIE CALLBACK PO SUKCESIE
                        onComplete(true, finalPath)

                    } catch (e: Exception) {
                        println("Błąd generowania raportu: ${e.message}")
                        e.printStackTrace()

                        // 💡 WYWOŁANIE CALLBACK PO BŁĘDZIE
                        onComplete(false, finalPath)
                    }
                }
            }
        } else {
            onComplete(false, "Anulowano")
        }
    }
}