java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "floria-api"

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
                username = env.fetch("REPSY_USERNAME", "")
                password = env.fetch("REPSY_PASSWORD", "")
            }
        }
    }
}

artifacts {
    add("default", tasks.named("remapJar"))
}

tasks.build {
    dependsOn(tasks.named("ktlintCheck"))
    dependsOn(tasks.named("detekt"))
    dependsOn(tasks.named("remapSourcesJar"))
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf(
        "version" to project.version,
        "loader_version" to project.property("loader_version"),
        "kotlin_loader_version" to project.property("kotlin_loader_version"),
        "minecraft_version" to project.property("minecraft_version"),
    )

    inputs.properties(props)

    filesMatching("fabric.mod.json") {
        expand(props)
    }
}
