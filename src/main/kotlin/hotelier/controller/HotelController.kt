package com.byron.hotelier.controller

import com.byron.hotelier.controller.dto.toResponse
import com.byron.hotelier.service.HotelFilterService
import com.byron.hotelier.service.HotelMergeService
import io.ktor.http.HttpStatusCode
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
    val filterService = HotelFilterService()

    get("/hotels") {
        val allHotels =
            coroutineScope {
                config.enabledSuppliers
                    .map { supplier -> async { dataSource.fetch(supplier) } }
                    .awaitAll()
            }

        // OUT OF SCOPE:
        // 1. This assumes not doing pagination in the supplier list
        // as pagination introduce a different complexity where we
        // may encounter the current hotel of supplier A appears in the next page of supplier B.
        // which we need a different "merge" mechanism
        val merged =
            allHotels
                .map { HotelMergeService.sort(it) }
                .reduce { acc, hotels -> mergeService.merge(acc, hotels) }

        val q = call.request.queryParameters["q"]
        // OUT OF SCOPE:
        // 1. but just putting it here for potential example,
        // where search like travellers=3+2 means 3 adults + 2 kids for better filtering
        // 2. there's the case where the customer can sort by price or points
        // but since the API response doesn't contain the price or point we can ignore that for now
        val travellersRaw = call.request.queryParameters["travellers"]
        val travellers =
            if (travellersRaw != null) {
                TravellerPreset.fromLabel(travellersRaw)
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Unknown travellers value '$travellersRaw'. Valid values: ${TravellerPreset.entries.map { it.label }}",
                    )
            } else {
                null
            }

        val filtered = filterService.filter(merged, HotelFilter(query = q, travellers = travellers))
        call.respond(filtered.map { it.toResponse() })
    }
}
