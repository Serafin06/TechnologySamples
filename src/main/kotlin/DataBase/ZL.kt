package pl.rafapp.techSam.DataBase

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Encja ZL - Zlecenia Laminacji
 * Mapuje tabelę ZL z bazy danych
 */
@Entity
@Table(name = "ZL")
data class ZL(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    val id: Int = 0,

    @Column(name = "NUMER", nullable = false)
    val numer: Int,

    @Column(name = "ODDZIAL", nullable = false)
    val oddzial: Byte,

    @Column(name = "ODDZIAL_W", nullable = false)
    val oddzialW: Byte,

    @Column(name = "ROK", nullable = false)
    val rok: Byte,

    @Column(name = "DATA", nullable = false)
    val data: LocalDateTime,

    @Column(name = "Z2", nullable = false)
    val z2: Boolean,

    @Column(name = "ID_MASZYNA", nullable = false)
    val idMaszyna: Int,

    @Column(name = "PRODUKT_1")
    val produkt1: Short?,

    @Column(name = "PRODUKT_2")
    val produkt2: Short?,

    @Column(name = "P_ILOSC_1", length = 30)
    val pIlosc1: String?,

    @Column(name = "P_ILOSC_2", length = 30, nullable = false)
    val pIlosc2: String,

    @Column(name = "RECEPTURA_1", length = 20)
    val receptura1: String?,

    @Column(name = "KOLORW_1", length = 20)
    val kolorw1: String?,

    @Column(name = "SZEROKOSC_1")
    val szerokosc1: Short?,

    @Column(name = "GRUBOSC_11")
    val grubosc11: Short?,

    @Column(name = "GRUBOSC_21")
    val grubosc21: Short?,

    @Column(name = "GRUBOSC_31")
    val grubosc31: Short?,

    @Column(name = "ILOSC")
    val ilosc: Double?,

    @Column(name = "WYKONANA")
    val wykonana: Double?,

    @Column(name = "TERMIN_ZAK")
    val terminZak: LocalDateTime?,

    @Column(name = "DATA_ZAK")
    val dataZak: LocalDateTime?,

    @Column(name = "STAN", nullable = false)
    val stan: Byte,

    @Column(name = "UWAGI", length = 1000)
    val uwagi: String?
)