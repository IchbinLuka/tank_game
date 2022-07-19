import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("js") version "1.7.0"
}

//group = "com.liorgonnen"
//version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":threejs_kt"))
    api(project(":statsjs_kt"))
}

kotlin {
    js {
        browser()
        binaries.executable()
    }
}

tasks.create("debug") {
    dependsOn.add("browserDevelopmentWebpack")
    dependsOn.add("browserDevelopmentRun")
    tasks.findByName("browserDevelopmentRun")?.mustRunAfter("browserDevelopmentWebpack")
}

tasks.withType(KotlinJsCompile::class.java).named("compileKotlinJs") {
    kotlinOptions.moduleKind = "commonjs"
}

// Fixes webpack-cli incompatibility by pinning the newest version.
rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
    versions.webpackDevServer.version = "4.8.0"
}