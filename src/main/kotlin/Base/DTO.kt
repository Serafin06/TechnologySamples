package pl.rafapp.techSam.Base

import java.time.LocalDateTime

/**
 * Informacje o próbce ze wszystkich tabel
 */
data class ProbkaDTO(
    val numer: Int,
    val oddzialNazwa: String,
    val dataZamowienia: LocalDateTime,
    val art: String?,
    val receptura: String?,
    val grubosc11: String?,
    val grubosc21: String?,
    val grubosc31: String?,
    val szerokosc: Short?,
    val statusZO: StatusInfo?,
    val statusZK: StatusInfo?,
    val statusZD: StatusInfo?
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