import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream

plugins {
    kotlin("js") version "1.7.21"
    id("io.kotest.multiplatform") version "5.5.0"
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
    js(IR) {
        browser()
        binaries.executable()
        dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4")
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

tasks.create("fixExternals") {
    val file = file("./build/externals/tank_game/src/lib.dom.kt")
    if (file.exists()) {
        val parent = file.parentFile
        file.delete()

        for (child in parent.listFiles()) {
            if (child.isDirectory) continue
            val stream = FileInputStream(child)
            val reader = BufferedReader(stream.reader())
            val lines = reader.readLines().toMutableList()

            var foundImports = false
            var modifiedFile = false

            for (value in lines.withIndex()) {
                val line = value.value
                val i = value.index
                if (!line.contains("import")) {
                    if (foundImports) break
                    else continue
                } else {
                    foundImports = true
                }
                if (line.contains("tsstdlib")) {
                    lines[i] = ""
                    modifiedFile = true
                }
            }
            reader.close()

            if (modifiedFile) {
                val out = FileOutputStream(child)
                out.bufferedWriter().write(lines.joinToString("\n"))
                out.close()
                file(child.absolutePath)
            }
        }
    } else {
        System.out.println("File not found")
    }
    outputs.upToDateWhen { false }
    outputs.cacheIf { false }
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