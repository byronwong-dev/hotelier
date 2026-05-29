package com.byron.hotelier.service

import com.byron.hotelier.adapter.domain.Amenities
import com.byron.hotelier.adapter.domain.Hotel
import com.byron.hotelier.adapter.domain.Images
import com.byron.hotelier.adapter.domain.normalise
import com.byron.hotelier.lib.haversineMeters
import com.byron.hotelier.lib.takeIfNotBlank

class HotelMergeService {
    companion object {
        private val comparator =
            compareBy<Hotel>(
                { it.id },
                { it.destinationId },
                { it.country.orEmpty() },
                { it.postalCode.orEmpty() },
                { it.address.orEmpty() },
            )

        fun sort(hotels: List<Hotel>): List<Hotel> = hotels.sortedWith(comparator)

        const val ALLOWED_DISTANCE_MATCH_METERS = 50.0
    }

    fun merge(
        existing: List<Hotel>,
        incoming: List<Hotel>,
    ): List<Hotel> {
        val result = existing.toMutableList()
        val index = buildIndex(result)

        for (hotel in incoming) {
            val idKey = hotel.id to hotel.destinationId
            val exactIdx = index[idKey]
            if (exactIdx != null) {
                result[exactIdx] = mergeFields(result[exactIdx], hotel)
            } else {
                val fuzzyIdx = result.indexOfFirst { matchesByLocation(it, hotel) }
                if (fuzzyIdx >= 0) {
                    result[fuzzyIdx] = mergeFields(result[fuzzyIdx], hotel)
                    index[result[fuzzyIdx].id to result[fuzzyIdx].destinationId] = fuzzyIdx
                } else {
                    result.add(hotel)
                    index[idKey] = result.lastIndex
                }
            }
        }

        return result
    }

    private fun matchesByLocation(
        a: Hotel,
        b: Hotel,
    ): Boolean {
        if (!nameRoughlyMatches(a.name, b.name)) return false
        if (!addressRoughlyMatches(a, b)) return false
        val latitudeA = a.latitude
        val longitudeA = a.longitude
        val latitudeB = b.latitude
        val longitudeB = b.longitude
        return !(
            latitudeA != null && longitudeA != null && latitudeB != null && longitudeB != null &&
                haversineMeters(
                    latitudeA,
                    longitudeA,
                    latitudeB,
                    longitudeB,
                ) > ALLOWED_DISTANCE_MATCH_METERS
        )
    }

    private fun nameRoughlyMatches(
        a: String,
        b: String,
    ): Boolean {
        val normalisedA = a.normalise()
        val normalisedB = b.normalise()
        return normalisedA.contains(normalisedB) || normalisedB.contains(normalisedA)
    }

    private fun addressRoughlyMatches(
        a: Hotel,
        b: Hotel,
    ): Boolean {
        val aa = a.address?.normalise() ?: return true
        val ba = b.address?.normalise() ?: return true
        return aa.contains(ba) || ba.contains(aa)
    }

    private fun mergeFields(
        a: Hotel,
        b: Hotel,
    ): Hotel {
        val primary = if (a.detailsCompletenessScore >= b.detailsCompletenessScore) a else b
        val secondary = if (primary === a) b else a
        return Hotel(
            id = primary.id.takeIf { it.isNotBlank() } ?: secondary.id,
            destinationId = primary.destinationId,
            name = if (a.name.length >= b.name.length) a.name else b.name,
            latitude = primary.latitude ?: secondary.latitude,
            longitude = primary.longitude ?: secondary.longitude,
            address = primary.address.takeIfNotBlank(secondary.address),
            city = primary.city.takeIfNotBlank(secondary.city),
            country = primary.country.takeIfNotBlank(secondary.country),
            postalCode = primary.postalCode.takeIfNotBlank(secondary.postalCode),
            description = listOfNotNull(a.description, b.description).maxByOrNull { it.length },
            amenities =
                Amenities(
                    general = (primary.amenities.general + secondary.amenities.general).distinct(),
                    room = (primary.amenities.room + secondary.amenities.room).distinct(),
                ),
            images =
                Images(
                    rooms = (primary.images.rooms + secondary.images.rooms).distinctBy { it.url },
                    amenities = (primary.images.amenities + secondary.images.amenities).distinctBy { it.url },
                    site = (primary.images.site + secondary.images.site).distinctBy { it.url },
                ),
            bookingConditions = (primary.bookingConditions + secondary.bookingConditions).distinct(),
        )
    }

    private fun buildIndex(hotels: List<Hotel>): MutableMap<Pair<String, Int>, Int> =
        hotels.mapIndexed { i, h -> (h.id to h.destinationId) to i }.toMap().toMutableMap()
}
