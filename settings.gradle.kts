pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.gradleup.nmcp.settings") version "1.6.1"
}

rootProject.name = "ImGuiB3D"

include("common")
include("versions:26.2")

nmcpSettings {
    centralPortal {
        username = providers.gradleProperty("maven_central_username").orNull
        password = providers.gradleProperty("maven_central_password").orNull
        publishingType = "USER_MANAGED"
    }
}