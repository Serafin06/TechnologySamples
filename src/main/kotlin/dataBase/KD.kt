package dataBase

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "KD")
data class KD(
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
    val rok: Byte,

    @Column(name = "DATA", nullable = false)
    val data: LocalDateTime,

    @Column(name = "Z2", nullable = false)
    val z2: Boolean,

    @Column(name = "Knt_GIDNumer")
    val kntGIDNumer: Int? = null,

    @Column(name = "ID_KONTRAHENTA", nullable = false)
    val idKontrahenta: Int,

    @Column(name = "TEMAT", length = 60)
    val temat: String? = null,

    @Column(name = "ID_RN", nullable = false)
    val idRn: Byte,

    @Column(name = "ID_MASZYNA", nullable = false)
    val idMaszyna: Byte,

    @Column(name = "ID_MASZYNY", length = 30)
    val idMaszyny: String? = null,

    @Column(name = "ID_REPER")
    val idReper: Byte? = null,

    @Column(name = "ID_STOPKA")
    val idStopka: Byte? = null,

    @Column(name = "WALEK")
    val walek: Byte? = null,

    @Column(name = "TOR")
    val tor: Byte? = null,

    @Column(name = "ID_ARTYKULU1")
    val idArtykulu1: Byte? = null,

    @Column(name = "ID_PRO1")
    val idPro1: Byte? = null,

    @Column(name = "INDEKS_KATALOGOWY1")
    val indeksKatalogowy1: Boolean? = null,

    @Column(name = "NAZWA_INDEKS1", length = 40)
    val nazwaIndeks1: String? = null,

    @Column(name = "OPIS1", length = 50)
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

    @Column(name = "TC")
    val tc: Double? = null,

    @Column(name = "TT")
    val tt: Double? = null,

    @Column(name = "CCN")
    val ccn: Double? = null,

    @Column(name = "CCW")
    val ccw: Double? = null,

    @Column(name = "CTW")
    val ctw: Double? = null,

    @Column(name = "VWD")
    val vwd: Double? = null,

    @Column(name = "ILOSC_KOLOR")
    val iloscKolor: Byte? = null,

    @Column(name = "KOD_NAWOJU")
    val kodNawoju: Byte? = null,

    @Column(name = "OBWOD_WAL")
    val obwodWal: Double? = null,

    @Column(name = "MARGINES_L")
    val marginesL: Double? = null,

    @Column(name = "MARGINES_P")
    val marginesP: Double? = null,

    @Column(name = "KOD_KRESKOWY")
    val kodKreskowy: Long? = null,

    @Column(name = "KOLOR", nullable = false, columnDefinition = "varchar(max)")
    val kolor: String,

    @Column(name = "FOTOPOLIMERY")
    val fotopolimery: Boolean? = null,

    @Column(name = "HD", nullable = false)
    val hd: Boolean,

    @Column(name = "TOR_F")
    val torF: Byte? = null,

    @Column(name = "KOLOR_R", columnDefinition = "varchar(max)")
    val kolorR: String? = null,

    @Column(name = "RECEPTURA_K", columnDefinition = "varchar(max)")
    val recepturaK: String? = null,

    @Column(name = "SCHEMAT", columnDefinition = "varchar(max)" )
    val schemat: String? = null,

    @Column(name = "FORMA", nullable = false, columnDefinition = "varchar(max)")
    val forma: String,

    @Column(name = "PLYTY", nullable = false, columnDefinition = "varchar(max)")
    val plyty: String,


    @Column(name = "ANILOX", nullable = false, columnDefinition = "varchar(max)")
    val anilox: String,

    @Column(name = "ID_ANILOX", columnDefinition = "varchar(max)")
    val idAnilox: String? = null,

    @Column(name = "LAB", columnDefinition = "varchar(max)")
    val lab: String? = null,

    @Column(name = "OPRACOWAL", nullable = false)
    val opracowal: Int,

    @Column(name = "DATA_OPRACOWAL", nullable = false)
    val dataOpracowal: LocalDateTime,

    @Column(name = "ZATWIERDZAL")
    val zatwierdzal: Int? = null,

    @Column(name = "DATA_ZATWIERDZAL")
    val dataZatwierdzal: LocalDateTime? = null,

    @Column(name = "UWAGI", columnDefinition = "varchar(max)")
    val uwagi: String? = null,

    @Column(name = "OSTATNI", nullable = false, columnDefinition = "varchar(max)")
    val ostatni: String,

    @Column(name = "SEMAFOR", nullable = false)
    val semafor: Boolean,

    @Column(name = "SEMAFOR2")
    val semafor2: Int? = null,

    @Column(name = "STATUS")
    val status: Byte? = null
)
