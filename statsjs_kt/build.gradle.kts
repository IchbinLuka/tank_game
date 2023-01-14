plugins {
    kotlin("js")
}

group = "com.liorgonnen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(npm("stats-js", "1.0.1", generateExternals = false))
}

kotlin {
    js {
        browser()
        binaries.executable()
    }
}

tasks.findByName("browserDevelopmentRun")!!.dependsOn("developmentExecutableCompileSync")