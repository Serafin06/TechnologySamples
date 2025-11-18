package pl.rafapp.techSam.Base

import java.time.LocalDateTime

/**
 * DTO próbki - zawiera dane z ZO, ZK, ZD, ZL oraz Technologia
 */
data class ProbkaDTO(
    // Podstawowe info
    val numer: Int,
    val oddzial: Byte,
    val rok: Byte,
    val oddzialNazwa: String,
    val dataZamowienia: LocalDateTime,

    // Dane techniczne z ZO
    val art: String?,
    val receptura: String?,
    val grubosc11: String?,
    val grubosc21: String?,
    val grubosc31: String?,
    val szerokosc: Short?,

    // Statusy
    val statusZO: StatusInfo?,
    val statusZD: StatusInfo?,
    val statusZL: List<StatusInfo>?,
    val statusZK: StatusInfo?,

    // 4 nowe pola tekstowe
    val opis: String? = null,
    val dodtkoweInformacje: String? = null,
    val uwagi: String? = null,
    val testy: String? = null
)

/**
 * Status zamówienia w danej tabeli
 */
data class StatusInfo(
    val stan: Byte,
    val stanNazwa: String,
    val ilosc: Double?,
    val wykonana: Double?,
    val terminZak: LocalDateTime?,
    val dataZak: LocalDateTime?
)