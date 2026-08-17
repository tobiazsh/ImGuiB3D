plugins {
    id("fabric-conventions")
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

group = rootProject.property("maven_group").toString()
version = rootProject.property("mod_version").toString()

val imguiVersion = project.property("imgui_version").toString()
val log4jVersion = project.property("log4j_version").toString()

val imguiDeps = listOf(
    "io.github.spair:imgui-java-binding:$imguiVersion",
    "io.github.spair:imgui-java-natives-windows:$imguiVersion",
    "io.github.spair:imgui-java-natives-linux:$imguiVersion",
    "io.github.spair:imgui-java-natives-macos:$imguiVersion",
)

val imguiLwjgl = "io.github.spair:imgui-java-lwjgl3:$imguiVersion"

extra["withImGui"] = { consumer: (String) -> Unit, lwjgl: (String) -> Unit ->
    imguiDeps.forEach(consumer)
    lwjgl(imguiLwjgl)
}

dependencies {
    imguiDeps.forEach { api(it) }
    api(imguiLwjgl) {
        exclude(group = "org.lwjgl")
    }

    compileOnlyApi("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    implementation("org.jspecify:jspecify:1.0.1")

    // Non MC deps

    // Logging
    implementation(platform("org.apache.logging.log4j:log4j-bom:${log4jVersion}"))
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")

    // Misc
    implementation("com.google.guava:guava:33.6.0-jre")
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}



