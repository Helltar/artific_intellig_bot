plugins {
    id("com.github.johnrengelman.shadow") version ("7.1.2")
    kotlin("jvm") version "1.9.10"
    application
}

group = "com.helltar"
version = "0.9.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.annimon:tgbots-module:6.5.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("org.json:json:20230227")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.7")

    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.0-Beta")
    implementation("org.xerial:sqlite-jdbc:3.40.1.0")

    implementation("com.google.cloud:google-cloud-translate:2.23.0")
    implementation("com.google.cloud:google-cloud-texttospeech:2.24.0")
}

application {
    mainClass.set("com.helltar.aibot.ArtificIntelligBot")
}