plugins {
    kotlin("jvm") version "2.2.20"
}

group = "pl.rafapp.techSam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.hibernate.orm:hibernate-core:6.6.4.Final") // Hibernate
    implementation("org.mysql:mysql-connector-java:8.1.0") // sterownik do PostgreSQL, zmień jeśli masz inną bazę
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(24)
}
