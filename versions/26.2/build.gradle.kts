plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("fabric-conventions")
    java
    `java-library`
}

// Version-specific
val loaderVersion = project.property("loader_version").toString()
val fabricApiVersion = project.property("fabric_api_version").toString()
val minecraftVersion = project.property("minecraft_version").toString()

// Shared
val modAuthor = rootProject.property("mod_author").toString()
val modId = rootProject.property("mod_id").toString()
val modName = rootProject.property("mod_name").toString()
val modVersion = rootProject.property("mod_version").toString()

base {
    archivesName = project.property("archives_base_name").toString()
}

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath = file("src/main/resources/imguib3d.classtweaker")
}

repositories {
    // Add repositories to retrieve artifacts from in here.
}

dependencies {
    api(project(":common"))
    implementation(project(":common"))
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")

    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
}

// Configure resource processing in Kotlin DSL
tasks.named<ProcessResources>("processResources") {
    val modAuthors = modAuthor.split(",").map { it.trim() }

    inputs.property("mod_version", modVersion)
    inputs.property("minecraft_version", minecraftVersion)
    inputs.property("loader_version", loaderVersion)
    inputs.property("authors", modAuthors)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "mod_version" to modVersion,
                "minecraft_version" to minecraftVersion,
                "loader_version" to loaderVersion,
                "authors" to modAuthors
            )
        )
    }
}

val targetJavaVersion = 25
tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { fileName -> "${fileName}_${project.property("archives_base_name")}" }
    }
}

//// configure the maven publication
//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            artifactId = project.property("archives_base_name").toString()
//            from(components["java"])
//        }
//    }
//
//    repositories {
//        // Add publishing repositories here.
//    }
//}
