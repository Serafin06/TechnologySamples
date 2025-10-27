package pl.rafapp.techSam.DataBase

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ZO")
data class ZO(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    val id: Int? = null,

    @Column(name = "NUMER", nullable = false)
    val numer: Int,

    @Column(name = "ODDZIAL", nullable = false)
    val oddzial: Byte,

    @Column(name = "ODDZIAL_W", nullable = false)
    val oddzialW: Byte,

    @Column(name = "ROK", nullable = false)
    val rok: Int,

    @Column(name = "DATA", nullable = false)
    val data: LocalDateTime,

    @Column(name = "ID_FAKTURA")
    val idFaktura: Int? = null,

    @Column(name = "ID_KONTRAHENTA", nullable = false)
    val idKontrahenta: Int,

    @Column(name = "Knt_GIDNumer")
    val kntGIDNumer: Int? = null,

    @Column(name = "ID_ARTYKULU1")
    val idArtykulu1: Byte? = null,

    @Column(name = "ID_PRO1", nullable = false)
    val idPro1: Byte,

    @Column(name = "OKRES_PRZYDATNOSCI", nullable = false)
    val okresPrzydatnosci: Byte,

    @Column(name = "[ORDER]", length = 20)
    val order: String? = null,

    @Column(name = "ART", length = 20)
    val art: String? = null,

    @Column(name = "EAN", length = 20)
    val ean: String? = null,

    @Column(name = "INDEKS_KATALOGOWY1")
    val indeksKatalogowy1: Boolean? = null,

    @Column(name = "NAZWA_INDEKS1", length = 40)
    val nazwaIndeks1: String? = null,

    @Column(name = "OPIS1", length = 40)
    val opis1: String? = null,

    @Column(name = "SZEROKOSC_1")
    val szerokosc1: Short? = null,

    @Column(name = "SZEROKOSCR_1")
    val szerokoscR1: Short? = null,

    @Column(name = "GRUBOSC_11", length = 10)
    val grubosc11: String? = null,

    @Column(name = "GRUBOSC_21", length = 10)
    val grubosc21: String? = null,

    @Column(name = "GRUBOSC_31", length = 10)
    val grubosc31: String? = null,

    @Column(name = "WYSOKOSC_1")
    val wysokosc1: Short? = null,

    @Column(name = "FALDAD_1", length = 10)
    val faldaD1: String? = null,

    @Column(name = "FALDAB_1", length = 10)
    val faldaB1: String? = null,

    @Column(name = "FALDAG_1", length = 10)
    val faldaG1: String? = null,

    @Column(name = "GLEBOKOSC_1")
    val glebokosc1: Short? = null,

    @Column(name = "ZAKLADKA_1", length = 10)
    val zakladka1: String? = null,

    @Column(name = "RECEPTURA_1", length = 20)
    val receptura1: String? = null,

    @Column(name = "KOLORW_1", length = 20)
    val kolorW1: String? = null,

    @Column(name = "ILOSC")
    val ilosc: Double? = null,

    @Column(name = "TECH")
    val tech: Double? = null,

    @Column(name = "JM1", length = 10)
    val jm1: String? = null,

    @Column(name = "JM2", length = 10)
    val jm2: String? = null,

    @Column(name = "TOLERANCJA")
    val tolerancja: Short? = null,

    @Column(name = "WYKONANA")
    val wykonana: Double? = null,

    @Column(name = "BRAKOW")
    val brakow: Double? = null,

    @Column(name = "TERMIN_ROZ")
    val terminRoz: LocalDateTime? = null,

    @Column(name = "TERMIN_ZAK")
    val terminZak: LocalDateTime? = null,

    @Column(name = "DATA_ROZ")
    val dataRoz: LocalDateTime? = null,

    @Column(name = "DATA_ZAK")
    val dataZak: LocalDateTime? = null,

    @Column(name = "DATA_SPRZ")
    val dataSprz: LocalDateTime? = null,

    @Column(name = "OPRACOWAL", nullable = false)
    val opracowal: Int,

    @Column(name = "DATA_OPRACOWAL")
    val dataOpracowal: LocalDateTime? = null,

    @Column(name = "ZATWIERDZAL")
    val zatwierdzal: Int? = null,

    @Column(name = "DATA_ZATWIERDZAL")
    val dataZatwierdzal: LocalDateTime? = null,

    @Column(name = "KOLEJNOSC")
    val kolejnosc: Byte? = null,

    @Column(name = "PROBA")
    val proba: Byte? = null,

    @Column(name = "STAN", nullable = false)
    val stan: Byte,

    @Column(name = "OSTATNI", nullable = false, columnDefinition = "varchar(max)")
    val ostatni: String,

    @Column(name = "SEMAFOR", nullable = false)
    val semafor: Boolean,

    @Column(name = "SEMAFOR2")
    val semafor2: Int? = null
)
