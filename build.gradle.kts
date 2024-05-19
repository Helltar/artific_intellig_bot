plugins {
    kotlin("jvm") version "1.9.24"
    application
}

group = "com.helltar"
version = "0.9.8"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.annimon:tgbots-module:7.1.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1")
    implementation("org.json:json:20231013")

    implementation("org.xerial:sqlite-jdbc:3.44.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.12")
}

application {
    mainClass.set("com.helltar.aibot.ArtificIntelligBot")
}