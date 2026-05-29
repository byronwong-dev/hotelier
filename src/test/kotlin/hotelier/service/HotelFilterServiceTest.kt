package com.byron.hotelier.service

import com.byron.hotelier.adapter.domain.Amenities
import com.byron.hotelier.adapter.domain.Hotel
import com.byron.hotelier.adapter.domain.Images
import com.byron.hotelier.controller.HotelFilter
import com.byron.hotelier.controller.TravellerPreset
import kotlin.test.Test
import kotlin.test.assertEquals

class HotelFilterServiceTest {
    private val service = HotelFilterService()

    private fun hotel(
        id: String,
        name: String,
        description: String? = null,
        address: String? = null,
    ) = Hotel(
        id = id,
        destinationId = 1,
        name = name,
        latitude = null,
        longitude = null,
        address = address,
        city = null,
        country = null,
        postalCode = null,
        description = description,
        amenities = Amenities(emptyList(), emptyList()),
        images = Images(emptyList(), emptyList(), emptyList()),
        bookingConditions = emptyList(),
    )

    private val hotels =
        listOf(
            hotel("A", name = "Beach Villas Singapore", description = "A tropical paradise.", address = "8 Sentosa Gateway"),
            hotel("B", name = "Hilton Tokyo", description = "Modern city hotel in Shinjuku.", address = "6-6-2 Nishi-Shinjuku"),
            hotel("C", name = "InterContinental", description = null, address = null),
        )

    @Test
    fun `null query returns all hotels`() {
        val result = service.filter(hotels, HotelFilter(query = null, travellers = null))
        assertEquals(3, result.size)
    }

    @Test
    fun `blank query returns all hotels`() {
        val result = service.filter(hotels, HotelFilter(query = "   ", travellers = null))
        assertEquals(3, result.size)
    }

    @Test
    fun `query matches on name case-insensitively`() {
        val result = service.filter(hotels, HotelFilter(query = "hilton", travellers = null))
        assertEquals(1, result.size)
        assertEquals("B", result[0].id)
    }

    @Test
    fun `query matches on description case-insensitively`() {
        val result = service.filter(hotels, HotelFilter(query = "TROPICAL", travellers = null))
        assertEquals(1, result.size)
        assertEquals("A", result[0].id)
    }

    @Test
    fun `query matches on address case-insensitively`() {
        val result = service.filter(hotels, HotelFilter(query = "sentosa", travellers = null))
        assertEquals(1, result.size)
        assertEquals("A", result[0].id)
    }

    @Test
    fun `query with no match returns empty list`() {
        val result = service.filter(hotels, HotelFilter(query = "paris", travellers = null))
        assertEquals(0, result.size)
    }

    @Test
    fun `hotel with null description and address is matched by name only`() {
        val result = service.filter(hotels, HotelFilter(query = "intercontinental", travellers = null))
        assertEquals(1, result.size)
        assertEquals("C", result[0].id)
    }

    @Test
    fun `travellers preset does not filter hotels`() {
        val result = service.filter(hotels, HotelFilter(query = null, travellers = TravellerPreset.LARGE_FAMILY))
        assertEquals(3, result.size)
    }

    @Test
    fun `query and travellers preset combined — only query filters`() {
        val result = service.filter(hotels, HotelFilter(query = "tokyo", travellers = TravellerPreset.COUPLE_TWO_CHILDREN))
        assertEquals(1, result.size)
        assertEquals("B", result[0].id)
    }

    @Test
    fun `TravellerPreset fromLabel returns correct preset`() {
        assertEquals(TravellerPreset.COUPLE, TravellerPreset.fromLabel("2+0"))
        assertEquals(TravellerPreset.FAMILY, TravellerPreset.fromLabel("3+2"))
        assertEquals(null, TravellerPreset.fromLabel("99+0"))
    }

    @Test
    fun `all TravellerPreset labels are parseable`() {
        TravellerPreset.entries.forEach { preset ->
            assertEquals(preset, TravellerPreset.fromLabel(preset.label))
        }
    }
}
