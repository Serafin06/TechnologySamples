package base

import androidx.compose.ui.graphics.painter.BitmapPainter
import dataBase.Kontrahent
import dataBase.Technologia
import dataBase.ZD
import dataBase.ZK
import dataBase.ZL
import dataBase.ZO
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Mapper - Przekształca encje na DTO (Single Responsibility)
 */
class ProbkaMapper {

    fun toProbkaDTO(
        zo: ZO,
        statusResolver: StatusResolver
    ): ProbkaDTO {
        // 1. Wyciągamy dane bezpośrednio z obiektu 'zo'
        // Konwertujemy Set na List, aby zachować zgodność z resztą kodu
        val zdList = zo.statusZD?.toList() ?: emptyList()
        val zlList = zo.statusZL?.toList() ?: emptyList()
        val zkList = zo.statusZK?.toList() ?: emptyList()

        // 2. Dla technologii, zakładając relację 1-do-1, bierzemy pierwszy element z Setu
        val technologia = zo.technologia

        // Automatyczna flaga 'produce' z statusu ZO
        val produceFlag = when (zo.stan) {
            0.toByte() -> true  // Wykonane
            1.toByte(), 2.toByte() -> false  // W realizacji, Planowane
            else -> null  // Inne statusy
        }

        return ProbkaDTO(
            numer = zo.numer,
            oddzial = zo.oddzialW,
            rok = zo.rok.toByte(),
            oddzialNazwa = getOddzialNazwa(zo.oddzialW),
            dataZamowienia = zo.data,
            kontrahentNazwa = zo.kontrahent?.nazwa ?: "Nieznany",
            art = zo.art,
            receptura = zo.receptura1,
            grubosc11 = zo.grubosc11,
            grubosc21 = zo.grubosc21,
            grubosc31 = zo.grubosc31,
            szerokosc = zo.szerokosc1,
            ilosc = zo.ilosc,
            jm = zo.jm1,
            nazwa = zo.opis1,

            // Statusy z wszystkich tabel
            statusZO = createStatusInfo(
                zo.stan,
                zo.ilosc,
                zo.jm1,
                zo.wykonana,
                zo.terminZak,
                zo.dataZak,
                statusResolver
            ),
            statusZD = zdList.firstOrNull()?.let { zd ->
                createStatusInfo(
                    zd.stan,
                    zd.ilosc,
                    zd.jm1,
                    zd.wykonana,
                    zd.terminZak,
                    zd.dataZak,
                    statusResolver
                )
            },
            statusZL = zlList.map { zl ->
                createStatusInfo(zl.stan, zl.ilosc, zl.jm1, zl.wykonana, zl.terminZak, zl.dataZak, statusResolver)
            }.takeIf { it.isNotEmpty() },

            statusZK = zkList.firstOrNull()?.let { zk ->
                createStatusInfo(
                    zk.stan,
                    zk.ilosc,
                    zk.jm1,
                    zk.wykonana,
                    zk.terminZak,
                    zk.dataZak,
                    statusResolver
                )
            },

            opis = technologia?.opis,
            dodtkoweInformacje = technologia?.dodatkoweInfo,
            uwagi = technologia?.uwagi,
            testy = technologia?.testy,
            produce = technologia?.produce,
            send = technologia?.send,
            tested = technologia?.tested
        )
    }

    private fun createStatusInfo(
        stan: Byte,
        ilosc: Double?,
        jm: String?,
        wykonana: Double?,
        terminZak: LocalDateTime?,
        dataZak: LocalDateTime?,
        statusResolver: StatusResolver
    ): StatusInfo {
        return StatusInfo(
            stan = stan,
            stanNazwa = statusResolver.getStatusName(stan),
            ilosc = ilosc,
            jm = jm,
            wykonana = wykonana,
            terminZak = terminZak,
            dataZak = dataZak
        )
    }

    /**
     * Mapuje ODDZIAL_W na nazwę oddziału
     */
    private fun getOddzialNazwa(oddzialW: Byte): String {
        return when (oddzialW.toInt()) {
            11 -> "Ignatki"
            12 -> "Tychy"
            else -> "Nieznany ($oddzialW)"
        }
    }
}

/**
 * Resolver statusów - Strategy Pattern
 */
class StatusResolver {

    private val statusMap = mapOf(
        0.toByte() to "Wykonane",
        1.toByte() to "W realizacji",
        2.toByte() to "Planowane",
        3.toByte() to "Wstrzymane",
        4.toByte() to "Anulowane",
        5.toByte() to "Do weryfikacji"
    )

    fun getStatusName(stan: Byte): String {
        return statusMap[stan] ?: "Nieznany status ($stan)"
    }

    fun addStatus(stan: Byte, nazwa: String) {
        (statusMap as MutableMap)[stan] = nazwa
    }
}

fun Int.toBigDecimalId(): BigDecimal = BigDecimal.valueOf(this.toLong())