package base

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
    val kontrahentNazwa: String,

    // Dane techniczne z ZO
    val art: String?,
    val receptura: String?,
    val grubosc11: String?,
    val grubosc21: String?,
    val grubosc31: String?,
    val szerokosc: Short?,
    val ilosc: Double?,
    val jm: String?,
    val nazwa: String?,

    // Statusy
    val statusZO: StatusInfo?,
    val statusZD: StatusInfo?,
    val statusZL: List<StatusInfo>?,
    val statusZK: StatusInfo?,

    // tabela technologia - mozliwy zapis i update danych
    val opis: String? = null,
    val dodtkoweInformacje: String? = null,
    val uwagi: String? = null,
    val testy: String? = null,
    val produce: Boolean? = null,
    val send: Boolean? = null,
    val tested: Boolean? = null
)

/**
 * Status zamówienia w danej tabeli
 */
data class StatusInfo(
    val stan: Byte,
    val stanNazwa: String,
    val ilosc: Double?,
    val jm: String?,
    val wykonana: Double?,
    val terminZak: LocalDateTime?,
    val dataZak: LocalDateTime?
)

/**
 * DTO dla widoku magazynu
 */

data class MagazynDTO(
    val numer: Int,
    val kontrahentNazwa: String,
    val tested: Boolean?,
    val strukturaMag: String?,
    val skladMag: String?,
    val szerokoscMag: String?,
    val iloscMag: String?,
    val uwagiMag: String?,
    val dataProdukcjiMag: LocalDateTime?,
    val dataAktualizacjiMag: LocalDateTime?
)
