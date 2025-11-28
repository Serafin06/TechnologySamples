package report

import base.ProbkaService
import base.StatusResolver
import java.time.format.DateTimeFormatter

class ReportService(
    private val probkaService: ProbkaService,
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun przygotujDaneDoRaportu(filter: RaportFilter): List<ReportDTO> {
        // 1. Pobierz surowe dane (możesz tu dodać parametr monthsBack jeśli potrzeba)
        val daneZBazy = probkaService.getProbki(monthBack = 12)

        // 2. Filtrowanie
        val przefiltrowane = daneZBazy.filter { dto ->
            // Filtr Oddziału
            val zgodnoscOddzialu = filter.oddzialNazwa == null || dto.oddzialNazwa == filter.oddzialNazwa

            // Filtr "Otwarte" (Planowane [2] lub W realizacji [1])
            val zgodnoscStatusu = if (filter.tylkoOtwarte) {
                val s = dto.statusZO?.stan
                s == 1.toByte() || s == 2.toByte()
            } else {
                true
            }

            zgodnoscOddzialu && zgodnoscStatusu
        }

        // 3. Mapowanie na format raportu
        return przefiltrowane.map { dto ->
            // Rozbicie listy ZL na konkretne pozycje (max 2)
            val zl1 = dto.statusZL?.getOrNull(0)
            val zl2 = dto.statusZL?.getOrNull(1)

            ReportDTO(
                numerZlecenia = dto.numer,
                kontrahent = dto.kontrahentNazwa,
                nazwaProbki = dto.nazwa ?: "",

                statusZO = dto.statusZO?.stanNazwa ?: "Brak",
                terminZO = dto.statusZO?.terminZak?.format(dateFormatter) ?: "-",

                statusZD = dto.statusZD?.stanNazwa ?: "-",

                // Formatowanie ZL: "NazwaStatusu" (lub puste jeśli brak)
                statusZL1 = zl1?.stanNazwa ?: "-",
                statusZL2 = zl2?.stanNazwa ?: "-",

                art = dto.art ?: "",
                receptura = dto.receptura ?: "",
                szerokosc = dto.szerokosc?.toString() ?: "-",

                grubosc1 = dto.grubosc11 ?: "",
                grubosc2 = dto.grubosc21 ?: "",
                grubosc3 = dto.grubosc31 ?: "",

                opis = dto.opis ?: "",
                dodatkoweInfo = dto.dodtkoweInformacje ?: ""
            )
        }.sortedBy { it.numerZlecenia } // Sortowanie np. po numerze
    }
}