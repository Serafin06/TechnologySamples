package report

import java.io.File

interface ReportGenerator {
    /**
     * Generuje raport do pliku.
     * @param dane Dane do umieszczenia w raporcie.
     * @param sciezkaPliku Ścieżka, gdzie ma zostać zapisany plik.
     * @return true jeśli zapis udany, false w przeciwnym razie.
     */
    fun generujReport(dane: List<ReportDTO>, sciezkaPliku: String): File
}



