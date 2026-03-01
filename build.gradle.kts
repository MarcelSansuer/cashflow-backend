import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
}

group = "my"
version = "0.0.1-SNAPSHOT"
description = "cachflow-backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("downloadOpenApiYaml") {
    group = "documentation"
    description = "Downloads the OpenAPI YAML from the running Spring Boot app into resources/static"

    val outputDir = "src/main/resources/static"
    val outputFile = "$outputDir/openapi.yaml"
    val openApiUrl = "http://localhost:8080/v3/api-docs.yaml" // Springdoc Endpoint

    doLast {
        println("Downloading OpenAPI spec from $openApiUrl ...")

        try {
            val url = URL(openApiUrl)
            val content = url.readText()

            // Ensure directory exists
            Files.createDirectories(Paths.get(outputDir))

            // Write file
            Files.write(Paths.get(outputFile), content.toByteArray())

            println("OpenAPI spec saved to $outputFile")
        } catch (e: Exception) {
            println("Error downloading OpenAPI spec: ${e.message}")
            throw e
        }
    }
}
