import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.3.10" apply false

    id("fabric-loom") version "1.15-SNAPSHOT" apply false
    id("dev.detekt") version "2.0.0-alpha.2" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0" apply false
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("maven-publish")
}

allprojects {
    group = property("maven_group") as String
    version = property("mod_version") as String
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "fabric-loom")
    apply(plugin = "dev.detekt")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        maven("https://keksuccino.github.io/maven")
    }

    dependencies {
        add("minecraft", "com.mojang:minecraft:${project.property("minecraft_version")}")
        add("mappings", "net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
        add("modImplementation", "net.fabricmc:fabric-loader:${project.property("loader_version")}")
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
        add("modImplementation", "net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    }

    configure<KtlintExtension> {
        version.set("1.5.0")
        android.set(false)
        outputToConsole.set(true)
        coloredOutput.set(true)
        ignoreFailures.set(false)
        reporters {
            reporter(ReporterType.HTML)
        }
        filter {
            exclude("**/generated/**")
            include("**/*.kt")
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}
