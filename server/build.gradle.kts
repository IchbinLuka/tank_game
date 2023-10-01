val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val koin_ktor: String by project

plugins {
    kotlin("multiplatform")
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.kodein.mock.mockmp") version "1.15.0"
    application
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}


mockmp {
    usesHelper = true
}

group = "com.ichbinluka"
version = "0.0.1"
application {
    mainClass.set("com.ichbinluka.ServerKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
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
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("com.benasher44:uuid:0.7.1")
                implementation(project(mapOf("path" to ":common")))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("io.ktor:ktor-server-websockets:$ktor_version")
                implementation("io.ktor:ktor-server-resources:$ktor_version")
                implementation("io.ktor:ktor-server-cors:$ktor_version")
                implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty")
                implementation("io.insert-koin:koin-logger-slf4j:$koin_ktor")
                implementation("io.ktor:ktor-network-tls-certificates")
            }
        }
        val jvmTest by getting
        if (!isMingwX64) {
            val nativeMain by getting {
                dependencies {
                    implementation("io.ktor:ktor-server-cio:$ktor_version")

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
