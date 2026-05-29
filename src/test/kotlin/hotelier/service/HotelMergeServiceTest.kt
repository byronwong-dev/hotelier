package com.byron.hotelier.service

import com.byron.hotelier.adapter.domain.Amenity
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import com.byron.hotelier.adapter.acme.dto.Hotel as AcmeHotel
import com.byron.hotelier.adapter.paperflies.dto.Hotel as PaperfliesHotel
import com.byron.hotelier.adapter.patagonia.dto.Hotel as PatagoniaHotel

class HotelMergeServiceTest {
    private val service = HotelMergeService()

    private val acmeHotels =
        loadResource("/acme_hotels.json")
            .let { Json.decodeFromString<List<AcmeHotel>>(it) }
            .map { it.toDomain() }

    private val paperfliesHotels =
        loadResource("/paperflies_hotels.json")
            .let { Json.decodeFromString<List<PaperfliesHotel>>(it) }
            .map { it.toDomain() }

    private val patagoniaHotels =
        loadResource("/patagonia_hotels.json")
            .let { Json.decodeFromString<List<PatagoniaHotel>>(it) }
            .map { it.toDomain() }

    private fun loadResource(path: String) = this::class.java.getResourceAsStream(path)!!.bufferedReader().readText()

    private fun fullMerge() =
        listOf(acmeHotels, paperfliesHotels, patagoniaHotels)
            .reduce { acc, hotels ->
                service.merge(acc, HotelMergeService.sort(hotels))
            }

    @Test
    fun `sort orders hotels by id lexicographically`() {
        val sorted = HotelMergeService.sort(acmeHotels)
        assertEquals(3, sorted.size)
        assertEquals("SjyX", sorted[0].id)
        assertEquals("f8c9", sorted[1].id)
        assertEquals("iJhz", sorted[2].id)
    }

    @Test
    fun `merge two suppliers collapses same hotel by exact id`() {
        val merged =
            listOf(acmeHotels, paperfliesHotels)
                .reduce { acc, hotels ->
                    service.merge(acc, HotelMergeService.sort(hotels))
                }
        assertEquals(3, merged.size)
    }

    @Test
    fun `full merge of three suppliers produces 3 unique hotels`() {
        assertEquals(3, fullMerge().size)
    }

    @Test
    fun `merged iJhz has all fields correctly merged from three suppliers`() {
        val beachVillas = fullMerge().first { it.id == "iJhz" }

        // scalar fields
        assertEquals("iJhz", beachVillas.id)
        assertEquals(5432, beachVillas.destinationId)
        assertEquals("Beach Villas Singapore", beachVillas.name)
        assertEquals(1.264751, beachVillas.latitude) // from acme (paperflies has none)
        assertEquals(103.824006, beachVillas.longitude) // from acme
        assertEquals("8 Sentosa Gateway, Beach Villas, 098269", beachVillas.address) // paperflies primary
        assertEquals("Singapore", beachVillas.city) // from acme (paperflies has none)
        assertEquals("Singapore", beachVillas.country) // paperflies primary
        assertEquals("098269", beachVillas.postalCode) // from acme (paperflies has none)
        // description from patagonia (longest across all three suppliers)
        val expectedDescription = patagoniaHotels.first { it.id == "iJhz" }.description
        assertEquals(expectedDescription, beachVillas.description)

        // amenities — union of all three suppliers, deduped
        assertEquals(
            listOf(
                Amenity.OUTDOOR_POOL,
                Amenity.INDOOR_POOL,
                Amenity.BUSINESS_CENTER,
                Amenity.CHILDCARE,
                Amenity.WIFI,
                Amenity.DRY_CLEANING,
                Amenity.BREAKFAST,
            ),
            beachVillas.amenities.general,
        )
        assertEquals(
            listOf(
                Amenity.TV,
                Amenity.COFFEE_MACHINE,
                Amenity.KETTLE,
                Amenity.HAIR_DRYER,
                Amenity.IRON,
                Amenity.AIR_CONDITIONING,
                Amenity.BATHTUB,
            ),
            beachVillas.amenities.room,
        )

        // images — 0qZF/2.jpg in both paperflies and patagonia, deduped to 1
        assertEquals(3, beachVillas.images.rooms.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/2.jpg", beachVillas.images.rooms[0].url)
        assertEquals("Double room", beachVillas.images.rooms[0].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/3.jpg", beachVillas.images.rooms[1].url)
        assertEquals("Double room", beachVillas.images.rooms[1].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/4.jpg", beachVillas.images.rooms[2].url)
        assertEquals("Bathroom", beachVillas.images.rooms[2].description)
        assertEquals(2, beachVillas.images.amenities.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/0.jpg", beachVillas.images.amenities[0].url)
        assertEquals("RWS", beachVillas.images.amenities[0].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/6.jpg", beachVillas.images.amenities[1].url)
        assertEquals("Sentosa Gateway", beachVillas.images.amenities[1].description)
        assertEquals(1, beachVillas.images.site.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/1.jpg", beachVillas.images.site[0].url)
        assertEquals("Front", beachVillas.images.site[0].description)

        // booking conditions — all 5 from paperflies
        assertEquals(5, beachVillas.bookingConditions.size)
        assertEquals("Pets are not allowed.", beachVillas.bookingConditions[1])
        assertEquals("WiFi is available in all areas and is free of charge.", beachVillas.bookingConditions[2])
        assertEquals("Free private parking is possible on site (reservation is not needed).", beachVillas.bookingConditions[3])
    }

    @Test
    fun `merged SjyX has all fields correctly merged from two suppliers`() {
        val intercontinental = fullMerge().first { it.id == "SjyX" }

        // scalar fields
        assertEquals("SjyX", intercontinental.id)
        assertEquals(5432, intercontinental.destinationId)
        assertEquals("InterContinental Singapore Robertson Quay", intercontinental.name) // acme (longer name)
        assertEquals(null, intercontinental.latitude) // neither supplier has coords
        assertEquals(null, intercontinental.longitude)
        assertEquals("1 Nanson Rd, Singapore 238909", intercontinental.address) // paperflies primary
        assertEquals("Singapore", intercontinental.city) // from acme (paperflies has none)
        assertEquals("Singapore", intercontinental.country) // paperflies primary
        assertEquals("238909", intercontinental.postalCode) // from acme (paperflies has none)
        val expectedDescription = paperfliesHotels.first { it.id == "SjyX" }.description
        assertEquals(expectedDescription, intercontinental.description)

        // amenities — union of acme + paperflies, deduped
        assertEquals(
            listOf(
                Amenity.OUTDOOR_POOL, Amenity.BUSINESS_CENTER, Amenity.CHILDCARE, Amenity.PARKING,
                Amenity.BAR, Amenity.DRY_CLEANING, Amenity.WIFI, Amenity.BREAKFAST, Amenity.CONCIERGE,
            ),
            intercontinental.amenities.general,
        )
        assertEquals(
            listOf(Amenity.AIR_CONDITIONING, Amenity.MINIBAR, Amenity.TV, Amenity.BATHTUB, Amenity.HAIR_DRYER),
            intercontinental.amenities.room,
        )

        // images — all from paperflies, acme has none
        assertEquals(2, intercontinental.images.rooms.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/Sjym/i93_m.jpg", intercontinental.images.rooms[0].url)
        assertEquals("Double room", intercontinental.images.rooms[0].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/Sjym/i94_m.jpg", intercontinental.images.rooms[1].url)
        assertEquals("Bathroom", intercontinental.images.rooms[1].description)
        assertEquals(0, intercontinental.images.amenities.size)
        assertEquals(4, intercontinental.images.site.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/Sjym/i1_m.jpg", intercontinental.images.site[0].url)
        assertEquals("Restaurant", intercontinental.images.site[0].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/Sjym/i2_m.jpg", intercontinental.images.site[1].url)
        assertEquals("Hotel Exterior", intercontinental.images.site[1].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/Sjym/i5_m.jpg", intercontinental.images.site[2].url)
        assertEquals("Entrance", intercontinental.images.site[2].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/Sjym/i24_m.jpg", intercontinental.images.site[3].url)
        assertEquals("Bar", intercontinental.images.site[3].description)

        // booking conditions — neither supplier has any for SjyX
        assertEquals(0, intercontinental.bookingConditions.size)
    }

    @Test
    fun `merged f8c9 has all fields correctly merged from three suppliers`() {
        val hilton = fullMerge().first { it.id == "f8c9" }

        // scalar fields
        assertEquals("f8c9", hilton.id)
        assertEquals(1122, hilton.destinationId)
        assertEquals("Hilton Shinjuku Tokyo", hilton.name) // acme (longer name, ties with patagonia go to existing)
        assertEquals(35.6926, hilton.latitude) // from patagonia (acme + paperflies have none)
        assertEquals(139.690965, hilton.longitude) // from patagonia
        assertEquals("160-0023, SHINJUKU-KU, 6-6-2 NISHI-SHINJUKU, JAPAN", hilton.address)
        assertEquals("Tokyo", hilton.city) // from acme (paperflies has none)
        assertEquals("Japan", hilton.country) // paperflies primary
        assertEquals("160-0023", hilton.postalCode) // from acme (paperflies has none)
        val expectedDescription = paperfliesHotels.first { it.id == "f8c9" }.description
        assertEquals(expectedDescription, hilton.description)

        // amenities — union of all three suppliers, deduped
        assertEquals(
            listOf(
                Amenity.INDOOR_POOL,
                Amenity.BUSINESS_CENTER,
                Amenity.WIFI,
                Amenity.OUTDOOR_POOL,
                Amenity.DRY_CLEANING,
                Amenity.BREAKFAST,
                Amenity.BAR,
            ),
            hilton.amenities.general,
        )
        assertEquals(
            listOf(Amenity.TV, Amenity.AIR_CONDITIONING, Amenity.MINIBAR, Amenity.BATHTUB, Amenity.HAIR_DRYER),
            hilton.amenities.room,
        )

        // images — paperflies rooms + patagonia rooms (all unique URLs)
        assertEquals(4, hilton.images.rooms.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i1_m.jpg", hilton.images.rooms[0].url)
        assertEquals("Suite", hilton.images.rooms[0].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i15_m.jpg", hilton.images.rooms[1].url)
        assertEquals("Double room", hilton.images.rooms[1].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i10_m.jpg", hilton.images.rooms[2].url)
        assertEquals("Suite", hilton.images.rooms[2].description)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i11_m.jpg", hilton.images.rooms[3].url)
        assertEquals("Suite - Living room", hilton.images.rooms[3].description)
        assertEquals(1, hilton.images.amenities.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i57_m.jpg", hilton.images.amenities[0].url)
        assertEquals("Bar", hilton.images.amenities[0].description)
        assertEquals(1, hilton.images.site.size)
        assertEquals("https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i55_m.jpg", hilton.images.site[0].url)
        assertEquals("Bar", hilton.images.site[0].description)

        // booking conditions — all 6 from paperflies
        assertEquals(6, hilton.bookingConditions.size)
        assertEquals("Pets are not allowed.", hilton.bookingConditions[1])
        assertEquals(
            "Private parking is possible on site (reservation is not needed) and costs JPY 1500 per day.",
            hilton.bookingConditions[3],
        )
    }
}
