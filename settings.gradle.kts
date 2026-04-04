rootProject.name = "Floria"

include("api")
include("core")

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}
