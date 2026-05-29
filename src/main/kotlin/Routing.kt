package com.byron

import com.byron.hotelier.controller.HotelConfiguration
import com.byron.hotelier.controller.SupplierDataSource
import com.byron.hotelier.controller.hotelRoutes
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

fun Application.configureRouting() {
    install(ContentNegotiation) { json() }

    val httpClient =
        HttpClient(CIO) {
            install(ClientContentNegotiation) { json() }
        }
    val config = HotelConfiguration(environment.config)
    val dataSource = SupplierDataSource(httpClient, config.maxRetries)

    routing {
        get("/") { call.respondText("go to /hotels for the actual response") }
        hotelRoutes(dataSource, config)
    }
}
