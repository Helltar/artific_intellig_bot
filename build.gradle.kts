plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.helltar"
version = "0.9.20"

repositories {
    mavenCentral()
}

object Versions {
    const val tgbotsModule = "7.9.0"
    const val jacksonModuleKotlin = "2.14.2"
    const val fuel = "2.3.1"
    const val dotenvKotlin = "6.4.1"
    const val imgscalrLib = "4.2"
    const val exposedVersion = "0.52.0"
    const val postgresql = "42.6.0"
    const val coroutinesCoreJvm = "1.8.1"
    const val kotlinxSerializationJson = "1.7.1"
    const val logbackClassic = "1.5.6"
}

dependencies {
    implementation("com.annimon:tgbots-module:${Versions.tgbotsModule}") {
        exclude("org.telegram", "telegrambots-webhook")
    }

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jacksonModuleKotlin}")
    implementation("com.github.kittinunf.fuel:fuel:${Versions.fuel}")
    implementation("io.github.cdimascio:dotenv-kotlin:${Versions.dotenvKotlin}")
    implementation("org.imgscalr:imgscalr-lib:${Versions.imgscalrLib}")

    implementation("org.jetbrains.exposed:exposed-core:${Versions.exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-dao:${Versions.exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-java-time:${Versions.exposedVersion}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${Versions.exposedVersion}")
    implementation("org.postgresql:postgresql:${Versions.postgresql}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.coroutinesCoreJvm}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationJson}")

    implementation("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
}

application {
    mainClass.set("com.helltar.aibot.bot.ArtificIntelligBot")
}