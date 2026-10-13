plugins {
    `java-library`
    `kotlin-dsl`
}

group = rootProject.property("maven_group").toString()
version = rootProject.property("mod_version").toString()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
}