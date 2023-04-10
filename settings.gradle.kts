pluginManagement {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "tank_game"

include("threejs_kt")
include("statsjs_kt")
include("common")
include("server")

