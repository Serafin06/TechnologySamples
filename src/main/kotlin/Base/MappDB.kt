package pl.rafapp.techSam.Base

import pl.rafapp.techSam.DataBase.*
import java.time.LocalDateTime


// 🔄 Mapper - Przekształcanie encji na DTO

/**
 * Mapper zgodny z Single Responsibility Principle
 */
class ProbkaMapper {

    fun toProbkaDTO(
        zo: ZO,
        zkList: List<ZK>,
        zdList: List<ZD>,
        statusResolver: StatusResolver
    ): ProbkaDTO {
        return ProbkaDTO(
            numer = zo.numer,
            oddzialNazwa = getOddzialNazwa(zo.idPro1),
            dataZamowienia = zo.data,
            art = zo.art,
            receptura = zo.receptura1,
            grubosc11 = zo.grubosc11,
            grubosc21 = zo.grubosc21,
            grubosc31 = zo.grubosc31,
            szerokosc = zo.szerokosc1,
            statusZO = createStatusInfo(
                zo.stan,
                zo.ilosc,
                zo.wykonana,
                zo.terminZak,
                zo.dataZak,
                statusResolver
            ),
            statusZK = zkList.firstOrNull()?.let { zk ->
                createStatusInfo(
                    zk.stan,
                    zk.ilosc,
                    zk.wykonana,
                    zk.terminZak,
                    zk.dataZak,
                    statusResolver
                )
            },
            statusZD = zdList.firstOrNull()?.let { zd ->
                createStatusInfo(
                    zd.stan,
                    zd.ilosc,
                    zd.wykonana,
                    zd.terminZak,
                    zd.dataZak,
                    statusResolver
                )
            }
        )
    }

    private fun createStatusInfo(
        stan: Byte,
        ilosc: Double?,
        wykonana: Double?,
        terminZak: LocalDateTime?,
        dataZak: LocalDateTime?,
        statusResolver: StatusResolver
    ): StatusInfo {
        return StatusInfo(
            stan = stan,
            stanNazwa = statusResolver.getStatusName(stan),
            ilosc = ilosc,
            wykonana = wykonana,
            terminZak = terminZak,
            dataZak = dataZak
        )
    }

    private fun getOddzialNazwa(idPro1: Byte): String {
        return when (idPro1.toInt()) {
            1 -> "Ignatki"
            10 -> "Tychy"
            else -> "Nieznany ($idPro1)"
        }
    }
}

// 🎯 Status Resolver - Strategy Pattern

class StatusResolver {

    private val statusMap = mapOf(
        0.toByte() to "Nowe",
        1.toByte() to "W realizacji",
        2.toByte() to "Zakończone",
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