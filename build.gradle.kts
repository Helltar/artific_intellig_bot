plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.helltar"
version = "0.9.96"

repositories {
    mavenCentral()
}

object Versions {
    const val TGBOTS_MODULE = "8.0.0"
    const val JACKSON_MODULE_KOTLIN = "2.14.2"
    const val FUEL = "2.3.1"
    const val DOTENV_KOTLIN = "6.4.1"
    const val IMGSCALR_LIB = "4.2"
    const val EXPOSED = "0.57.0"
    const val POSTGRESQL = "42.7.3"
    const val COROUTINES_CORE_JVM = "1.8.1"
    const val KOTLINX_SERIALIZATION_JSON = "1.7.1"
    const val LOGBACK_CLASSIC = "1.5.16"
    const val KOTLIN_LOGGING = "7.0.3"
}

dependencies {
    implementation("com.annimon:tgbots-module:${Versions.TGBOTS_MODULE}") {
        exclude("org.telegram", "telegrambots-webhook")
    }

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON_MODULE_KOTLIN}")
    implementation("com.github.kittinunf.fuel:fuel:${Versions.FUEL}")
    implementation("io.github.cdimascio:dotenv-kotlin:${Versions.DOTENV_KOTLIN}")
    implementation("org.imgscalr:imgscalr-lib:${Versions.IMGSCALR_LIB}")

    implementation("org.jetbrains.exposed:exposed-core:${Versions.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-dao:${Versions.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-java-time:${Versions.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${Versions.EXPOSED}")
    implementation("org.postgresql:postgresql:${Versions.POSTGRESQL}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.COROUTINES_CORE_JVM}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLINX_SERIALIZATION_JSON}")

    implementation("io.github.oshai:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING}")
    implementation("ch.qos.logback:logback-classic:${Versions.LOGBACK_CLASSIC}")
}

application {
    mainClass.set("com.helltar.aibot.bot.ArtificIntelligBot")
}
