package dataBase

import jakarta.persistence.*

@Entity
@Table(name = "KONTRAHENT", schema = "dbo")
data class Kontrahent(
    @Id
    @Column(name = "ID_KONTRAHENTA")
    val idKontrahenta: Int,

    @Column(name = "NAZWA")
    val nazwa: String,
)
