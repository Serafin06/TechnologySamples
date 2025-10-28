package pl.rafapp.techSam.Base

// Factory do tworzenia serwisów - Dependency Inversion Principle

object ProbkaServiceFactory {

    fun createProbkaService(sessionFactory: org.hibernate.SessionFactory): ProbkaService {
        val repository = ProbkaRepositoryImpl(sessionFactory)
        val mapper = ProbkaMapper()
        val statusResolver = StatusResolver()

        return ProbkaServiceImpl(repository, mapper, statusResolver)
    }
}