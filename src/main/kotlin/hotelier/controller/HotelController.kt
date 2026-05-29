package com.byron.hotelier.controller

import com.byron.hotelier.controller.dto.toResponse
import com.byron.hotelier.service.HotelMergeService
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun Route.hotelRoutes(
    dataSource: SupplierDataSource,
    config: HotelConfiguration,
) {
    val mergeService = HotelMergeService()

    get("/hotels") {
        val allHotels =
            coroutineScope {
                config.enabledSuppliers
                    .map { supplier -> async { dataSource.fetch(supplier) } }
                    .awaitAll()
            }

        val merged =
            allHotels
                .map { HotelMergeService.sort(it) }
                .reduce { acc, hotels -> mergeService.merge(acc, hotels) }

        call.respond(merged.map { it.toResponse() })
    }
}
