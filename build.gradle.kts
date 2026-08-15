repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks.register("buildAll") {
    dependsOn(subprojects.map { it.tasks.named("build") })
}