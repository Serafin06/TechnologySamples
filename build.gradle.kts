import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.compose") version "1.7.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.2.20"
}

group = "pl.rafapp.techSam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation("org.hibernate.orm:hibernate-core:6.6.4.Final") // Hibernate
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.8.0.jre11") // sterownik do mySQL
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Connection Pool
    implementation("org.hibernate.orm:hibernate-hikaricp:6.6.4.Final")

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.16")

    //corutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")

    // szyfrowanie hasel
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "pl.rafapp.techSam.MainKt"

        nativeDistributions {
            modules("java.sql", "java.naming")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TechSamples"
            packageVersion = "1.0.0"

            description = "Program do zarządzania próbkami technologicznymi"
            copyright = "© 2025 RafApp"
            vendor = "RafApp"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
