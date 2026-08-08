plugins {
    java
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }
}

java {
    toolchain {
        // Use Java 17 for compatibility with typical Fabric toolchains and to match local JDKs
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}