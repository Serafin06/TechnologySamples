package report

import base.ProbkaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

enum class ExportType { EXCEL, PDF }

fun generujRaportAkcja(
    scope: CoroutineScope,
    exportType: ExportType,
    probkaService: ProbkaService
) {
    scope.launch {
        try {
            // Pokaż progress
            // viewModel.showProgress("Generowanie raportu...")

            val dane = withContext(Dispatchers.IO) {
                probkaService.getReportData() // Nowa metoda w service
            }

            val generator = when (exportType) {
                ExportType.EXCEL -> ReportExcelGenerator()
                ExportType.PDF -> ReportExcelGenerator()
            }

            val sciezka = withContext(Dispatchers.IO) {
                val timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                )
                val extension = if (exportType == ExportType.EXCEL) "xlsx" else "pdf"
                "raport_$timestamp.$extension"
            }

            val plik = withContext(Dispatchers.IO) {
                generator.generujReport(dane, sciezka)
            }

            // Pokaż sukces
            println("Raport wygenerowany: ${plik.absolutePath}")

        } catch (e: Exception) {
            println("Błąd generowania raportu: ${e.message}")
        }
    }
    }
