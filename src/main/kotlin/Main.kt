package pl.rafapp.techSam

import pl.rafapp.techSam.DataBase.HibernateConfig

fun main() {
    println("Test połączenia Hibernate z SQL Server...")

    val sessionFactory = HibernateConfig.sessionFactory
    val session = sessionFactory.openSession()

    try {
        session.beginTransaction()

        // Przykładowe zapytanie – tylko SELECT 1
        val result = session.createNativeQuery("SELECT 1").singleResult
        println("Połączenie OK, wynik testu: $result")

        session.transaction.commit()
    } catch (ex: Exception) {
        println("Błąd połączenia: ${ex.message}")
        session.transaction.rollback()
    } finally {
        session.close()
        HibernateConfig.shutdown()
    }
}