package report.magazyn

import base.MagazynDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import report.ExportType
import java.io.File
import java.time.format.DateTimeFormatter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/** Uruchamia dialog zapisu i generuje raport magazynu z opcjonalnym filtrem */
fun generujMagazynRaportAkcja(
    scope: CoroutineScope,
    type: ExportType,
    dane: List<MagazynDTO>,
    filter: MagazynRaportFilter,
    onComplete: (success: Boolean, path: String) -> Unit
) {
    scope.launch(Dispatchers.Swing) {
        val extension = if (type == ExportType.EXCEL) "xlsx" else "pdf"
        val fileChooser = JFileChooser().apply {
            selectedFile = File("Raport_Magazyn.$extension")
            fileFilter = FileNameExtensionFilter("Pliki $type (*.$extension)", extension)
        }

        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            onComplete(false, "Anulowano")
            return@launch
        }

        val sciezka = fileChooser.selectedFile.absolutePath.let {
            if (!it.endsWith(".$extension")) "$it.$extension" else it
        }

        launch {
            withContext(Dispatchers.IO) {
                try {
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val filtered = dane
                        .filter { filter.kontrahent == null || it.kontrahentNazwa == filter.kontrahent }
                        .filter { filter.sklad == null || it.skladMag == filter.sklad }
                        .filter { filter.szerokosc == null || it.szerokoscMag == filter.szerokosc }
                        .map { dto ->
                            MagazynReportDTO(
                                numer = dto.numer,
                                kontrahent = dto.kontrahentNazwa,
                                sklad = dto.skladMag ?: "-",
                                struktura = dto.strukturaMag ?: "-",
                                szerokosc = dto.szerokoscMag ?: "-",
                                ilosc = dto.iloscMag ?: "-",
                                uwagi = dto.uwagiMag ?: "-",
                                dataProdukcji = dto.dataProdukcjiMag?.format(dateFormatter) ?: "-"
                            )
                        }

                    when (type) {
                        ExportType.EXCEL -> MagazynExcelReportGenerator().generuj(filtered, sciezka)
                        ExportType.PDF -> MagazynPdfReportGenerator().generuj(filtered, sciezka)
                    }
                    onComplete(true, sciezka)
                } catch (e: Exception) {
                    onComplete(false, sciezka)
                }
            }
        }
    }
}