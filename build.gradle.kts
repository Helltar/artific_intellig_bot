plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("com.gradleup.shadow") version "8.3.6"
    application
}

group = "com.helltar"
version = "0.9.500"

repositories {
    mavenCentral()
}

object Versions {
    const val TGBOTS_MODULE = "8.0.0"
    const val JACKSON_MODULE_KOTLIN = "2.14.2"

    const val DOTENV_KOTLIN = "6.4.1"
    const val IMGSCALR_LIB = "4.2"

    const val EXPOSED = "1.0.0-beta-2"
    const val POSTGRESQL = "42.7.3"

    const val COROUTINES_CORE_JVM = "1.10.2"
    const val KOTLINX_SERIALIZATION_JSON = "1.8.1"

    const val KOTLIN_LOGGING = "7.0.7"
    const val LOGBACK_CLASSIC = "1.5.18"
}

dependencies {
    implementation("com.annimon:tgbots-module:${Versions.TGBOTS_MODULE}") {
        exclude("org.telegram", "telegrambots-webhook")
    }

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_MODULE_KOTLIN}")
    implementation("io.github.cdimascio:dotenv-kotlin:${Versions.DOTENV_KOTLIN}")
    implementation("org.imgscalr:imgscalr-lib:${Versions.IMGSCALR_LIB}")

    implementation("org.jetbrains.exposed:exposed-core:${Versions.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-dao:${Versions.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-java-time:${Versions.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${Versions.EXPOSED}")
    implementation("org.postgresql:postgresql:${Versions.POSTGRESQL}")

    implementation("io.ktor:ktor-client-cio:3.1.3")
    implementation("io.ktor:ktor-client-auth:3.1.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.COROUTINES_CORE_JVM}")


    implementation("io.github.oshai:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING}")
    implementation("ch.qos.logback:logback-classic:${Versions.LOGBACK_CLASSIC}")
}

application {
    mainClass.set("com.helltar.aibot.bot.ArtificIntelligBot")
}
