package base

import org.hibernate.SessionFactory

// Factory do tworzenia serwisów - Dependency Inversion Principle

object ProbkaServiceFactory {

    fun createProbkaService(sessionFactory: SessionFactory): ProbkaService {
        val repository = ProbkaRepositoryImpl(sessionFactory)
        val mapper = ProbkaMapper()
        val statusResolver = StatusResolver()

        return ProbkaServiceImpl(repository, mapper, statusResolver)
    }
}