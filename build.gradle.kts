import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("js") version "1.8.20"
    id("io.kotest.multiplatform") version "5.5.0"
    kotlin("plugin.serialization") version "1.7.20"
}

//group = "com.liorgonnen"
//version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":threejs_kt"))
    api(project(":statsjs_kt"))
    api(project(":common"))
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.0")
            testImplementation(kotlin("test-js"))
        }

    }

    sourceSets {
        dependencies {
            val pathToLocalNpmModule = rootProject.projectDir.resolve("rust/pkg").canonicalPath
            implementation(npm("rust_js", "file:$pathToLocalNpmModule", generateExternals = false))
        }

        test {
            dependencies {
                implementation("io.kotest:kotest-framework-engine-js:5.5.0")
                implementation("io.kotest:kotest-assertions-core-js:5.5.0")
                implementation("io.kotest:kotest-property-js:5.5.0")
            }
        }
    }
}


tasks.create("debug") {
    dependsOn.add("browserDevelopmentWebpack")
    dependsOn.add("browserDevelopmentRun")
    tasks.findByName("browserDevelopmentRun")?.mustRunAfter("browserDevelopmentWebpack")
}

tasks.findByName("run")?.dependsOn?.add("browserDevelopmentWebpack")

tasks.withType(KotlinJsCompile::class.java).named("compileKotlinJs") {
    kotlinOptions.moduleKind = "commonjs"
}

// Fixes webpack-cli incompatibility by pinning the newest version.
/*rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
    versions.webpackDevServer.version = "4.8.0"
}*/