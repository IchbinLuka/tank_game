package com.ichbinluka

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
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
    val keyStorePassword by parser.option(ArgType.String, fullName = "keystore-password").required()
    val keyAlias by parser.option(ArgType.String, fullName = "key-alias", shortName = "a").default("tank")
    val keyPassword by parser.option(ArgType.String, fullName = "key-password", shortName = "w").required()
    val port by parser.option(ArgType.Int, fullName = "port", shortName = "p").default(PORT)
    val selfSign by parser.option(ArgType.Boolean, fullName = "self-sign").default(false)

    parser.parse(args)

    val keyStoreFile = File(keyStoreFilePath)
    val keyStore = if (selfSign) {
        buildKeyStore {
            certificate(keyAlias) {
                password = keyPassword
                domains = listOf("localhost", "lukaborn.de")
            }
        }
    } else {
        KeyStore.getInstance("JKS").apply { load(keyStoreFile.inputStream(), keyStorePassword.toCharArray()) }
    }

    val environment = applicationEngineEnvironment {
        sslConnector(
            keyStore = keyStore,
            keyAlias = keyAlias,
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { keyPassword.toCharArray() }) {
            this.port = port
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }
    embeddedServer(Netty, environment).start(wait = true)
}