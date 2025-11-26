package dataBase

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ZD")
data class ZD(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    val id: Int? = null,

    @Column(name = "ODDZIAL", nullable = false)
    val oddzial: Byte,

    @Column(name = "ODDZIAL_W", nullable = false)
    val oddzialW: Byte,

    @Column(name = "NUMER", nullable = false)
    val numer: Int,

    @Column(name = "ROK", nullable = false)
    val rok: Byte,

    @Column(name = "DATA", nullable = false)
    val data: LocalDateTime,

    @Column(name = "Z2", nullable = false)
    val z2: Boolean,

    @Column(name = "ID_MASZYNA", nullable = false)
    val idMaszyna: Short,

    @Column(name = "PRODUKT_1", nullable = false)
    val produkt1: Byte,

    @Column(name = "P_ILOSC_1", length = 30)
    val pIlosc1: String? = null,

    @Column(name = "ID_ARTYKULU1")
    val idArtykulu1: Int? = null,

    @Column(name = "ID_PRO1", nullable = false)
    val idPro1: Byte,

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

    @Column(name = "GRUBOSC_11")
    val grubosc11: Short? = null,

    @Column(name = "GRUBOSC_21")
    val grubosc21: Short? = null,

    @Column(name = "GRUBOSC_31")
    val grubosc31: Short? = null,

    @Column(name = "WYSOKOSC_1")
    val wysokosc1: Short? = null,

    @Column(name = "FALDAD_1")
    val faldaD1: Short? = null,

    @Column(name = "FALDAB_1")
    val faldaB1: Short? = null,

    @Column(name = "FALDAG_1")
    val faldaG1: Short? = null,

    @Column(name = "GLEBOKOSC_1")
    val glebokosc1: Short? = null,

    @Column(name = "ZAKLADKA_1")
    val zakladka1: Short? = null,

    @Column(name = "RECEPTURA_1", length = 20)
    val receptura1: String? = null,

    @Column(name = "KOLORW_1", length = 20)
    val kolorW1: String? = null,

    @Column(name = "ID_ARTYKULU2")
    val idArtykulu2: Byte? = null,

    @Column(name = "ID_PRO2", nullable = false)
    val idPro2: Byte,

    @Column(name = "INDEKS_KATALOGOWY2")
    val indeksKatalogowy2: Boolean? = null,

    @Column(name = "NAZWA_INDEKS2", length = 40)
    val nazwaIndeks2: String? = null,

    @Column(name = "OPIS2", length = 40)
    val opis2: String? = null,

    @Column(name = "SZEROKOSC_2")
    val szerokosc2: Short? = null,

    @Column(name = "SZEROKOSCR_2")
    val szerokoscR2: Short? = null,

    @Column(name = "GRUBOSC_12")
    val grubosc12: Short? = null,

    @Column(name = "GRUBOSC_22")
    val grubosc22: Short? = null,

    @Column(name = "GRUBOSC_32")
    val grubosc32: Short? = null,

    @Column(name = "WYSOKOSC_2")
    val wysokosc2: Short? = null,

    @Column(name = "FALDAD_2")
    val faldaD2: Short? = null,

    @Column(name = "FALDAB_2")
    val faldaB2: Short? = null,

    @Column(name = "FALDAG_2")
    val faldaG2: Short? = null,

    @Column(name = "GLEBOKOSC_2")
    val glebokosc2: Short? = null,

    @Column(name = "ZAKLADKA_2")
    val zakladka2: Short? = null,

    @Column(name = "RECEPTURA_2", length = 20)
    val receptura2: String? = null,

    @Column(name = "KOLORW_2", length = 20)
    val kolorW2: String? = null,

    @Column(name = "ID_KARTA")
    val idKarta: Int? = null,

    @Column(name = "SREDNICA_NAWOJU", length = 30)
    val srednicaNawoju: String? = null,

    @Column(name = "WAGA_NAWOJU", length = 30)
    val wagaNawoju: String? = null,

    @Column(name = "UWAGI", length = 2000)
    val uwagi: String? = null,

    @Column(name = "PAKOWANIE", length = 30)
    val pakowanie: String? = null,

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

    @Column(name = "OPRACOWAL", nullable = false)
    val opracowal: Int,

    @Column(name = "DATA_OPRACOWAL")
    val dataOpracowal: LocalDateTime? = null,

    @Column(name = "ZATWIERDZAL")
    val zatwierdzal: Int? = null,

    @Column(name = "DATA_ZATWIERDZAL")
    val dataZatwierdzal: LocalDateTime? = null,

    @Column(name = "KOLEJNOSC")
    val kolejnosc: Short? = null,

    @Column(name = "STAN", nullable = false)
    val stan: Byte,

    @Column(name = "OSTATNI", nullable = false, length = 2200)
    val ostatni: String,

    @Column(name = "SEMAFOR", nullable = false)
    val semafor: Boolean,

    @Column(name = "SEMAFOR2")
    val semafor2: Int? = null,

    @Column(name = "OCENA")
    val ocena: Byte? = null,

    @Column(name = "OCENA_WYKONAL")
    val ocenaWykonal: Int? = null,

    @Column(name = "OCENA_DATA")
    val ocenaData: LocalDateTime? = null
)
