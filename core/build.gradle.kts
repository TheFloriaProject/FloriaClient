dependencies {
    modImplementation(project(":api"))
    include(project(":api"))

    implementation("io.github.classgraph:classgraph:${project.property("classgraph_version")}")
    include("io.github.classgraph:classgraph:${project.property("classgraph_version")}")

    modApi("de.keksuccino:mcef-fabric:${project.property("mcef_version")}")
}

base {
    archivesName.set("floria-${project.property("minecraft_version")}")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "floria"

            artifact(tasks.named("remapJar")) {
                builtBy(tasks.named("remapJar"))
            }

            artifact(tasks.named("remapSourcesJar")) {
                builtBy(tasks.named("remapSourcesJar"))
            }
        }
    }

    repositories {
        maven {
            url = uri("https://repo.repsy.io/lyranie/maven")
            credentials {
                username = env.REPSY_USERNAME.value
                password = env.REPSY_PASSWORD.value
            }
        }
    }
}

tasks.build {
    dependsOn(tasks.named("ktlintCheck"))
    dependsOn(tasks.named("detekt"))
    dependsOn(tasks.named("remapSourcesJar"))
}

tasks.named<ProcessResources>("processResources") {
    dependsOn("buildScreens")

    val props = mapOf(
        "version" to project.version,
        "loader_version" to project.property("loader_version"),
        "kotlin_loader_version" to project.property("kotlin_loader_version"),
        "minecraft_version" to project.property("minecraft_version"),
        "mcef_version" to project.property("mcef_version"),
    )

    inputs.dir(rootProject.file("screen/dist"))
    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }

    from(rootProject.file("screen/dist")) {
        into("assets/${project.property("mod_id")}/screen")
    }
}

tasks.runClient {
    dependsOn(tasks.check)
    dependsOn(tasks.build)
}

tasks.register<Exec>("buildScreens") {
    workingDir = rootProject.file("screen")

    val isWindows = System.getProperty("os.name").lowercase().contains("win")

    commandLine(
        if (isWindows) {
            listOf("cmd", "/c", "npm", "run", "build")
        } else {
            listOf("npm", "run", "build")
        },
    )
}
