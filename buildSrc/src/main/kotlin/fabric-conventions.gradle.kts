import kotlin.io.encoding.Base64

plugins {
    java
    `java-library`
    `maven-publish`
    signing
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("ImGuiB3D")
                description.set("A minecraft dependency mod to easily use ImGui in Minecraft.")
                url.set("https://github.com/tobiazsh/ImGuiB3D")

                licenses {
                    license {
                        name.set("LGPL-3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                    }

                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }

                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("tobiazsh")
                        name.set("Tobiazsh")
                        email.set("developer.tobiazsh@gmail.com")
                    }
                }

                scm {
                    url.set("https://github.com/tobiazsh/ImGuiB3D")
                    connection.set("scm:git:git://github.com/tobiazsh/ImGuiB3D.git")
                }
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

signing {
    setRequired({ gradle.taskGraph.hasTask(":publishAggregationToCentralPortal") })

    val signingKeyBase64 = findProperty("signing_key_base64") as String?
    val signingPassword = findProperty("signing_password") as String?

    if (signingKeyBase64 != null && signingPassword != null) {
        val signingKey = Base64.decode(signingKeyBase64).toString(Charsets.UTF_8)
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    sign(publishing.publications["maven"])
}