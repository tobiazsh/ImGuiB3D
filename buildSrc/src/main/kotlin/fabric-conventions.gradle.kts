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

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
}


java {
    withSourcesJar()
    withJavadocJar()
}