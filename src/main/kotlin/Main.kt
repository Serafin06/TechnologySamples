package pl.rafapp.techSam

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import pl.rafapp.techSam.Base.ProbkaServiceFactory
import pl.rafapp.techSam.DataBase.HibernateConfig
import pl.rafapp.techSam.UI.*


fun main() = application {
    val windowState = rememberWindowState(width = 1400.dp, height = 900.dp)

    Window(
        onCloseRequest = {
            HibernateConfig.shutdown()
            exitApplication()
        },
        state = windowState,
        title = "TechSam - Zarządzanie Próbkami"
    ) {
        val sessionFactory = remember { HibernateConfig.sessionFactory }
        val probkaService = remember { ProbkaServiceFactory.createProbkaService(sessionFactory) }
        val viewModel = remember { ProbkiViewModel(probkaService) }

        ProbkiScreen(viewModel)
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