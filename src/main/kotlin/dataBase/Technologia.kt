package dataBase

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "Technologia")
data class Technologia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    val id: Int = 0,

    // Klucz do powiązania z ZO
    @Column(name = "NUMER", nullable = false)
    val numer: Int,

    // 4 kolumny tekstowe
    @Column(name = "Opis", length = 500)
    var opis: String? = null,

    @Column(name = "Dodatkowe informacje", length = 500)
    var dodatkoweInfo: String? = null,

    @Column(name = "Uwagi", length = 500)
    var uwagi: String? = null,

    @Column(name = "Testy", length = 500)
    var testy: String? = null,

    @Column(name = "PRODUCE")
    val produce: Boolean? = null,

    @Column(name = "SEND")
    var send: Boolean? = null,

    @Column(name = "TESTED")
    var tested: Boolean? = null,

    @Column(name = "SKLAD_MAG", length = 200)
    var skladMag: String? = null,

    @Column(name = "SZEROKOSC_MAG", length = 200)
    var szerokoscMag: String? = null,

    @Column(name = "ILOSC_MAG", length = 200)
    var iloscMag: String? = null,

    @Column(name = "UWAGI_MAG", length = 1000)
    var uwagiMag: String? = null,

    @Column(name = "DATA_PRODUKCJI_MAG")
    var dataProdukcjiMag: LocalDateTime? = null,

    @Column(name = "DATA_AKTUALIZACJI_MAG")
    var dataAktualizacjiMag: LocalDateTime? = null,

    @Column(name = "MAG_AKTYWNY")
    var magAktywny: Boolean? = null
)