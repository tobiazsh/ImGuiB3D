plugins {
    id("fabric-conventions")
}

repositories {
    mavenCentral()
}

val imguiVersion = project.property("imgui_version").toString()
val log4jVersion = project.property("log4j_version").toString()

dependencies {
    api("io.github.spair:imgui-java-binding:$imguiVersion")
    api("io.github.spair:imgui-java-natives-windows:$imguiVersion")
    api("io.github.spair:imgui-java-natives-linux:$imguiVersion")
    api("io.github.spair:imgui-java-natives-macos:${imguiVersion}")
    api("io.github.spair:imgui-java-lwjgl3:$imguiVersion") {
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

