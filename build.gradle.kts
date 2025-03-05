plugins {
    id("java")
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "br.com.codingtest"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
    testImplementation("org.assertj:assertj-core:3.22.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "br.com.codingtest.GanhoCapitalMainKt"
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("app")  // Nome do JAR
    archiveClassifier.set("")   // Sem classifier (gera app.jar)
    archiveVersion.set("")      // Remove versão do nome
    mergeServiceFiles()
}
kotlin {
    jvmToolchain(17)
}
