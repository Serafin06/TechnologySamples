package pl.rafapp.techSam.DataBase

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvException
import io.github.cdimascio.dotenv.dotenv
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import java.util.Properties

object HibernateConfig {

    private val dotenv = dotenv {
        filename = "pas.env"
        ignoreIfMissing = false
    }

    val sessionFactory: SessionFactory by lazy {
        try {
            val configuration = Configuration()

            configuration.properties = createProperties()

            // ✅ Tylko potrzebne encje
            configuration.addAnnotatedClass(KD::class.java)
            configuration.addAnnotatedClass(ZD::class.java)
            configuration.addAnnotatedClass(ZK::class.java)
            configuration.addAnnotatedClass(ZO::class.java)

            configuration.buildSessionFactory()
        } catch (ex: Throwable) {
            throw ExceptionInInitializerError("Błąd inicjalizacji Hibernate: ${ex.message}")
        }
    }

    private fun createProperties(): Properties {
        val dbUrl = dotenv["DB_URL"] ?: error("Brak DB_URL w .env")
        val dbUser = dotenv["DB_USER"] ?: error("Brak DB_USER w .env")
        val dbPass = dotenv["DB_PASS"] ?: error("Brak DB_PASS w .env")

        return Properties().apply {
            setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
            setProperty("hibernate.connection.url", dbUrl)
            setProperty("hibernate.connection.username", dbUser)
            setProperty("hibernate.connection.password", dbPass)

            //setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect")
            setProperty("hibernate.hbm2ddl.auto", "validate")
            setProperty("hibernate.show_sql", "true")
            setProperty("hibernate.format_sql", "true")

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