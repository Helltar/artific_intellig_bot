import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version ("7.1.2")
    kotlin("jvm") version "1.7.21"
    application
}

group = "com.helltar"
version = "0.5.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.annimon:tgbots-module:6.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("org.json:json:20220924")

    implementation("org.xerial:sqlite-jdbc:3.40.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.helltar.artific_intellig_bot.ArtificIntelligBot")
}
