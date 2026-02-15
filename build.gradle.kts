plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.github.alwaysdarkk"
version = "1.0.0"

repositories {
    mavenCentral()

    maven(url = "https://maven.hytale.com/release")
}

dependencies {
    compileOnly(libs.hytale.server)

    implementation(libs.hibernate)
    implementation(libs.hikaricp)
    implementation(libs.mysql)
    implementation(libs.h2)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    filesMatching("manifest.json") {
        expand(
            "version" to project.version,
            "name" to project.name
        )
    }
}