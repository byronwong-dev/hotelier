package com.byron.hotelier.service

import com.byron.hotelier.adapter.domain.Hotel
import com.byron.hotelier.controller.HotelFilter

class HotelFilterService {
    fun filter(
        hotels: List<Hotel>,
        filter: HotelFilter,
    ): List<Hotel> {
        val q = filter.query?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } ?: return hotels
        return hotels.filter { hotel ->
            hotel.name.lowercase().contains(q) ||
                hotel.description?.lowercase()?.contains(q) == true ||
                hotel.address?.lowercase()?.contains(q) == true
        }
    }
}
