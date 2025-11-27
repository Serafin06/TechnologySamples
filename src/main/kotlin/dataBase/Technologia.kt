package dataBase

import jakarta.persistence.*

/**
 * Encja TODO_TABELA - Przechowuje 4 flagi tekstowe dla zamówień
 */
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

    @Column(name = "Produce")
    val produce: Boolean? = null,

    @Column(name = "Send")
    var send: Boolean? = null,

    @Column(name = "Tested")
    var tested: Boolean? = null
)