plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

group = "com.sedo"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

val jjwtVersion = "0.11.5"
val okHttpVersion = "4.12.0"

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.github.librepdf:openpdf:1.3.30")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("io.minio:minio:8.5.7")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}
