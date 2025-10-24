package pl.rafapp.techSam.DataBase

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import java.util.Properties

object HibernateConfig {

    val sessionFactory: SessionFactory by lazy {
        try {
            val configuration = Configuration()

            // Ustawienia w kodzie (bez hibernate.cfg.xml)
            configuration.properties = createProperties()

            // Dodaj encje
            configuration.addAnnotatedClass(KA::class.java)
            configuration.addAnnotatedClass(KD::class.java)
            configuration.addAnnotatedClass(KOD_NAWOJU::class.java)
            configuration.addAnnotatedClass(TypPalety::class.java)
            configuration.addAnnotatedClass(ZD::class.java)
            configuration.addAnnotatedClass(ZK::class.java)
            configuration.addAnnotatedClass(ZO::class.java)
            configuration.addAnnotatedClass(DtProperties::class.java)
            configuration.addAnnotatedClass(KartyWyrobu::class.java)
            configuration.addAnnotatedClass(WlasciwosciFizyczne::class.java)

            configuration.buildSessionFactory()
        } catch (ex: Throwable) {
            throw ExceptionInInitializerError("Błąd inicjalizacji Hibernate: ${ex.message}")
        }
    }

    private fun createProperties(): Properties {
        return Properties().apply {
            setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
            setProperty("hibernate.connection.url", "jdbc:postgresql://<HOST>:<PORT>/<DBNAME>")
            setProperty("hibernate.connection.username", "<USERNAME>")
            setProperty("hibernate.connection.password", "<PASSWORD>")

            setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
            setProperty("hibernate.hbm2ddl.auto", "validate")
            setProperty("hibernate.show_sql", "true")
            setProperty("hibernate.format_sql", "true")

            // Connection pool HikariCP
            setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
            setProperty("hibernate.hikari.minimumIdle", "5")
            setProperty("hibernate.hikari.maximumPoolSize", "20")
            setProperty("hibernate.hikari.idleTimeout", "300000")
        }
    }

    fun shutdown() {
        sessionFactory.close()
    }
}
