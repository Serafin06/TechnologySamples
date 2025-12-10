import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dataBase.HibernateConfig


fun main() = application {
    // Okno aplikacji jest tworzone od razu, ale jego zawartość (komponent App) jest prosta i szybka do wyrenderowania
    Window(
        onCloseRequest = {
            HibernateConfig.shutdown()
            exitApplication()
        },
        title = "Technologia - Zarządzanie Próbkami",
        state = rememberWindowState(width = 1600.dp, height = 900.dp)
    ) {
        App()
    }
}

fun test(){
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