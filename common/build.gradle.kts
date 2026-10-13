plugins {
    id("fabric-conventions")
    id("maven-publish-conventions")
    `kotlin-dsl`
    `java-library`
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



// -------------- Native build configuration --------------



val nativeLibName = "drawlist_accessor"
val nativeSourceDir = file("src/main/native")
val nativeBuildDir = layout.buildDirectory.dir("natives")
val jniHeaderDir = layout.buildDirectory.dir("generated/jni-headers")
val generatedResourcesDir = layout.buildDirectory.dir("generated/native-sources")

val hostOs = System.getProperty("os.name").lowercase()
val hostArch = System.getProperty("os.arch").lowercase()

val platformOs = when {
    "mac" in hostOs || "darwin" in hostOs -> "macos"
    "win" in hostOs -> "windows"
    else -> "linux"
}

val platformArch = if ("aarch64" in hostArch || "arm64" in hostArch) "aarch64" else "x86_64"
val platformDir = "$platformOs-$platformArch"
val libFileName = when (platformOs) {
    "windows" -> "$nativeLibName.dll"
    "macos" -> "lib$nativeLibName.dylib"
    else -> "lib$nativeLibName.so"
}

/**
 * Emitted jni-headers as a by-product of normal compilation.
 */
tasks.compileJava {
    options.headerOutputDirectory.set(jniHeaderDir)
}

/**
 * Configures the native build for the current system.
 */
val configureNatives = tasks.register("configureNatives", Exec::class) {
    group = "natives"
    description = "Configures ImGuiB3D's natives for the current system"
    dependsOn(tasks.compileJava) // headers must exist before cmake runs

    inputs.dir(nativeSourceDir)

    commandLine(
        "cmake",
        "-S", nativeSourceDir.absolutePath,
        "-B", nativeBuildDir.get().asFile.absolutePath,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DJNI_GENERATED_HEADERS_DIR=${jniHeaderDir.get().asFile.absolutePath}"
    )
}

/**
 * Builds the native library for the current system.
 */
val buildNatives = tasks.register("compileNatives", Exec::class) {
    group = "natives"
    description = "Compiles ImGuiB3D's natives for the current system"
    dependsOn(configureNatives)

    inputs.dir(nativeSourceDir)
    inputs.dir(jniHeaderDir)

    commandLine(
        "cmake", "--build", nativeBuildDir.get().asFile.absolutePath,
        "--config", "Release"
    )
}

/**
 * Copies the built native library to a location where NativeLoader can find it.
 */
val copyNatives = tasks.register("copyNatives", Copy::class) {
    group = "natives"
    description = "Places the built library where NativeLoader looks for it"
    dependsOn(buildNatives)

    from(nativeBuildDir) {
        include("**/$libFileName")   // MSVC puts it in Release/, other generators don't
        eachFile { relativePath = RelativePath(true, "natives", platformDir, name) }
    }
    includeEmptyDirs = false
    into(generatedResourcesDir)
}

sourceSets.main {
    resources.srcDir(copyNatives)
}

// Skip building natives if requested. Useful for CI, where we want to aggregate the prebuilt natives from other
// platforms instead of building them on the current platform.
if (providers.gradleProperty("skipNatives").isPresent) {
    listOf(configureNatives, buildNatives, copyNatives).forEach { it.configure { onlyIf { false } } }
}

// CI: natives that were built on the other platforms.
// Expects <dir>/natives/<os>-<arch>/<lib>, the same layout copyNatives produces.
providers.gradleProperty("prebuiltNativesDir").orNull?.let { prebuiltDir ->
    sourceSets.main { resources.srcDir(prebuiltDir) }
}

