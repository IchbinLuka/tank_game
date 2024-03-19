package com.ichbinluka

import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import sessions.PORT
import java.io.File
import java.security.KeyStore


fun main(args: Array<String>) {
    val parser = ArgParser("Tank Game Server")
    val keyStoreFilePath by parser.option(ArgType.String, fullName = "keystore", shortName = "k")
        .default("keystore.jks")
    val keyStorePassword by parser.option(ArgType.String, fullName = "keystore-password").default("123")
    val keyAlias by parser.option(ArgType.String, fullName = "key-alias", shortName = "a").default("tank")
    val keyPassword by parser.option(ArgType.String, fullName = "key-password", shortName = "w").default("123")
    val port by parser.option(ArgType.Int, fullName = "port", shortName = "p").default(PORT)
    val selfSign by parser.option(ArgType.Boolean, fullName = "self-sign").default(false)
    val useHttp by parser.option(ArgType.Boolean, fullName = "use-http").default(false)
    val discordClientId by parser.option(ArgType.String, fullName = "discord-client-id").required()
    val discordClientSecret by parser.option(ArgType.String, fullName = "discord-client-secret").required()

    parser.parse(args)

    val environment = applicationEngineEnvironment {
        if (!useHttp) {
            createHttpsConnector(
                keyStoreFilePath,
                keyStorePassword,
                keyAlias,
                keyPassword,
                port,
                selfSign
            )
        } else {
            createHttpConnector(port)
        }
        module {
            module(ApplicationConfig(discordClientId, discordClientSecret))
        }
    }

    embeddedServer(Netty, environment).start(wait = true)
}

fun ApplicationEngineEnvironmentBuilder.createHttpConnector(port: Int) {
    connector {
        this.port = port
    }
}

fun ApplicationEngineEnvironmentBuilder.createHttpsConnector(
    keyStoreFilePath: String,
    keyStorePassword: String,
    keyAlias: String,
    keyPassword: String,
    port: Int,
    selfSign: Boolean
) {
    val keyStoreFile = File(keyStoreFilePath)
    val keyStore = if (selfSign) {
        buildKeyStore {
            certificate(keyAlias) {
                password = keyPassword
                domains = listOf(
                    "localhost",
                    "lukaborn.de",
                    "discord.com",
                    "discordsays.com",
                    "discordapp.com",
                    "1219342090610278500.discordsays.com"
                )
            }
        }
    } else {
        KeyStore.getInstance("JKS").apply { load(keyStoreFile.inputStream(), keyStorePassword.toCharArray()) }
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = keyAlias,
        keyStorePassword = { keyStorePassword.toCharArray() },
        privateKeyPassword = { keyPassword.toCharArray() }) {
        this.port = port
        keyStorePath = keyStoreFile
    }
}