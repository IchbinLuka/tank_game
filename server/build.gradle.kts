val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_ktor: String by project

plugins {
    kotlin("multiplatform")
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization")
    application
}

group = "com.ichbinluka"
version = "0.0.1"
application {
    mainClass.set("com.ichbinluka.ServerKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    if (!isMingwX64) {
        val nativeTarget = when (hostOs) {
            "Mac OS X" -> macosX64("native")
            "Linux" -> linuxX64("native")
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }

        nativeTarget.apply {
            binaries {
                executable {
                    entryPoint = "main"
                }
            }
        }
    }

    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktor_version")
                implementation("ch.qos.logback:logback-classic:$logback_version")
                // Koin for Ktor
                implementation("io.insert-koin:koin-core:$koin_ktor")
                implementation("io.insert-koin:koin-ktor:$koin_ktor")
                // SLF4J Logger
                project(":common")
                implementation("io.ktor:ktor-server-websockets:$ktor_version")
                implementation("io.ktor:ktor-server-resources:$ktor_version")
                implementation("io.ktor:ktor-server-cio:$ktor_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktor_version")
                implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor")
            }
        }
        val jvmTest by getting
        if (!isMingwX64) {
            val nativeMain by getting {
                dependencies {

                }
            }
            val nativeTest by getting
        }
    }
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}
dependencies {
    implementation(project(mapOf("path" to ":common")))
}
