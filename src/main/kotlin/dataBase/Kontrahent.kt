package dataBase

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.math.BigDecimal

@Entity
@Immutable
@Table(name = "View_Kontrahent", schema = "dbo")
data class Kontrahent(
    @Id
    @Column(name = "ID_KONTRAHENTA")
    val idKontrahenta: BigDecimal,

    @Column(name = "NAZWA")
    val nazwa: String,
)
