package base

import org.hibernate.SessionFactory

// Factory do tworzenia serwisów - Dependency Inversion Principle

object ProbkaServiceFactory {

    // Przyjmuje repozytorium z zewnątrz, co pozwala na jego współdzielenie
    fun createProbkaService(
        repository: ProbkaRepository,
        mapper: ProbkaMapper = ProbkaMapper(), // Domyślne instancje, jeśli nie są potrzebne z zewnątrz
        statusResolver: StatusResolver = StatusResolver()
    ): ProbkaService {
        return ProbkaServiceImpl(repository, mapper, statusResolver)
    }
}