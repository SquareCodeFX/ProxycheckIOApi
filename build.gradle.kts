plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
    id("com.github.ben-manes.versions") version "0.50.0" // For dependency updates
    kotlin("jvm") version "1.9.0"
}

group = "com.github.SquareCodeFX"
version = "1.0.0"
description = "A Java/Kotlin client library for the ProxyCheck.io API v2"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib"))

    // HTTP client
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
    options.encoding = "UTF-8"
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "SquareCodeFX",
                "Built-By" to System.getProperty("user.name"),
                "Built-JDK" to System.getProperty("java.version"),
                "Created-By" to "Gradle ${gradle.gradleVersion}",
                "Automatic-Module-Name" to "io.proxycheck.api"
            )
        )
    }
}

// Configure JitPack specific tasks
tasks.register("javadocJar", Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

tasks.register("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// Only sign artifacts if signing keys are available
signing {
    setRequired(false) // Only sign when publishing to Maven Central
    sign(publishing.publications)
}

// Add a specific task for JitPack
tasks.register("prepareForJitPack") {
    dependsOn("build", "sourcesJar", "javadocJar", "publishToMavenLocal")
    doLast {
        println("JitPack build prepared successfully")
    }
}

// Task to update version in README.md
tasks.register("updateReadmeVersion") {
    doLast {
        val readmeFile = file("README.md")
        val readmeContent = readmeFile.readText()

        // Update version in Gradle dependency
        val updatedGradleContent = readmeContent.replace(
            Regex("implementation\\(\"com.github.SquareCodeFX:ProxycheckIOApi:[^\"]+\"\\)"),
            "implementation(\"com.github.SquareCodeFX:ProxycheckIOApi:${project.version}\")"
        )

        // Update version in Maven dependency
        val updatedContent = updatedGradleContent.replace(
            Regex("<version>[^<]+</version>\\s*</dependency>"),
            "<version>${project.version}</version>\n</dependency>"
        )

        readmeFile.writeText(updatedContent)
        println("Updated README.md with version ${project.version}")
    }
}

// Make the build task depend on updateReadmeVersion
tasks.named("build") {
    dependsOn("updateReadmeVersion")
}

// Task to generate a changelog from Git commits
tasks.register("generateChangelog") {
    doLast {
        val changelogFile = file("CHANGELOG.md")
        val changelog = StringBuilder()

        changelog.appendLine("# Changelog")
        changelog.appendLine("")
        changelog.appendLine("All notable changes to this project will be documented in this file.")
        changelog.appendLine("")
        changelog.appendLine("The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),")
        changelog.appendLine("and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).")
        changelog.appendLine("")

        // Add the current version
        // Use a simple approach for the date
        val currentDate = try {
            val process = ProcessBuilder("date", "+%Y-%m-%d")
                .redirectErrorStream(true)
                .start()

            val result = process.inputStream.bufferedReader().use { it.readText().trim() }
            process.waitFor()
            result
        } catch (e: Exception) {
            "YYYY-MM-DD" // Fallback if the date command fails
        }

        changelog.appendLine("## [${project.version}] - ${currentDate}")
        changelog.appendLine("")

        // Try to get Git commits for the current version
        try {
            val process = ProcessBuilder("git", "log", "--pretty=format:- %s", "-n", "10")
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().use { reader ->
                reader.lines().forEach { line ->
                    if (!line.contains("Update version") && !line.contains("Generate changelog")) {
                        changelog.appendLine(line)
                    }
                }
            }

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                changelog.appendLine("- Initial release")
            }
        } catch (e: Exception) {
            changelog.appendLine("- Initial release")
            println("Could not get Git commits: ${e.message}")
        }

        // Only write the file if it doesn't exist or if we're not in the initial release
        if (!changelogFile.exists() || project.version.toString() != "1.0.0") {
            changelogFile.writeText(changelog.toString())
            println("Generated CHANGELOG.md")
        } else {
            println("CHANGELOG.md already exists, skipping generation")
        }
    }
}

// Make the build task depend on generateChangelog
tasks.named("build") {
    dependsOn("generateChangelog")
}

// Configure the dependency updates plugin
tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    checkForGradleUpdate = true
    outputFormatter = "json,xml,html"
    outputDir = "build/reports/dependencyUpdates"
    reportfileName = "dependency-updates"

    // Reject versions with alpha, beta, rc, cr, m, preview, etc.
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

// Helper function to check if a version is stable
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return !isStable
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "ProxycheckIOApi"
            version = project.version.toString()

            from(components["java"])

            // Include the custom javadoc and sources JARs
            artifact(tasks["javadocJar"])
            artifact(tasks["sourcesJar"])

            pom {
                name.set("ProxyCheck.io API Client")
                description.set(project.description)
                url.set("https://github.com/SquareCodeFX/ProxycheckIOApi")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("SquareCodeFX")
                        name.set("SquareCodeFX")
                        url.set("https://github.com/SquareCodeFX")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/SquareCodeFX/ProxycheckIOApi.git")
                    developerConnection.set("scm:git:ssh://github.com:SquareCodeFX/ProxycheckIOApi.git")
                    url.set("https://github.com/SquareCodeFX/ProxycheckIOApi")
                }
            }
        }
    }
}
