package pl.rafapp.techSam.Base

import pl.rafapp.techSam.DataBase.*
import java.time.LocalDateTime

/**
 * Mapper - Przekształca encje na DTO (Single Responsibility)
 */
class ProbkaMapper {

    fun toProbkaDTO(
        zo: ZO,
        zdList: List<ZD>,
        zlList: List<ZL>,
        zkList: List<ZK>,
        technologia: Technologia?,
        statusResolver: StatusResolver
    ): ProbkaDTO {
        return ProbkaDTO(
            numer = zo.numer,
            oddzial = zo.oddzialW,
            rok = zo.rok.toByte(),
            oddzialNazwa = getOddzialNazwa(zo.oddzialW),
            dataZamowienia = zo.data,
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
            testy = technologia?.testy
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