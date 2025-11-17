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
    val statusZK: StatusInfo?,
    val statusZD: StatusInfo?,
    val statusZL: StatusInfo?,

    // 4 nowe pola tekstowe - TODO: zmienić nazwy
    val todoKolumna1: String? = null,
    val todoKolumna2: String? = null,
    val todoKolumna3: String? = null,
    val todoKolumna4: String? = null
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