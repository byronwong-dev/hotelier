package com.byron.hotelier.adapter.patagonia.dto

import com.byron.hotelier.adapter.domain.Amenity
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HotelTest {
    @Test
    fun `serialize and deserialize json`() {
        val json =
            """
            [
              {
                "id": "iJhz",
                "destination": 5432,
                "name": "Beach Villas Singapore",
                "lat": 1.264751,
                "lng": 103.824006,
                "address": "8 Sentosa Gateway, Beach Villas, 098269",
                "info": "Located at the western tip of Resorts World Sentosa, guests at the Beach Villas are guaranteed privacy while they enjoy spectacular views of glittering waters. Guests will find themselves in paradise with this series of exquisite tropical sanctuaries, making it the perfect setting for an idyllic retreat. Within each villa, guests will discover living areas and bedrooms that open out to mini gardens, private timber sundecks and verandahs elegantly framing either lush greenery or an expanse of sea. Guests are assured of a superior slumber with goose feather pillows and luxe mattresses paired with 400 thread count Egyptian cotton bed linen, tastefully paired with a full complement of luxurious in-room amenities and bathrooms boasting rain showers and free-standing tubs coupled with an exclusive array of ESPA amenities and toiletries. Guests also get to enjoy complimentary day access to the facilities at Asia’s flagship spa – the world-renowned ESPA.",
                "amenities": [
                  "Aircon",
                  "Tv",
                  "Coffee machine",
                  "Kettle",
                  "Hair dryer",
                  "Iron",
                  "Tub"
                ],
                "images": {
                  "rooms": [
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/2.jpg",
                      "description": "Double room"
                    },
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/4.jpg",
                      "description": "Bathroom"
                    }
                  ],
                  "amenities": [
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/0.jpg",
                      "description": "RWS"
                    },
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/0qZF/6.jpg",
                      "description": "Sentosa Gateway"
                    }
                  ]
                }
              },
              {
                "id": "f8c9",
                "destination": 1122,
                "name": "Hilton Tokyo Shinjuku",
                "lat": 35.6926,
                "lng": 139.690965,
                "address": null,
                "info": null,
                "amenities": null,
                "images": {
                  "rooms": [
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i10_m.jpg",
                      "description": "Suite"
                    },
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i11_m.jpg",
                      "description": "Suite - Living room"
                    }
                  ],
                  "amenities": [
                    {
                      "url": "https://d2ey9sqrvkqdfs.cloudfront.net/YwAr/i57_m.jpg",
                      "description": "Bar"
                    }
                  ]
                }
              }
            ]
            """.trimIndent()

        val hotels = Json.decodeFromString<List<Hotel>>(json)

        assertEquals(2, hotels.size)
        val first = hotels.first()
        assertEquals("iJhz", first.id)
        assertEquals(5432, first.destinationId)
        assertEquals("Beach Villas Singapore", first.name)
        assertEquals(1.264751, first.latitude)
        assertEquals(103.824006, first.longitude)
        assertEquals(7, first.amenities!!.size)
        assertEquals(2, first.images.rooms.size)
        assertEquals("Double room", first.images.rooms.first().description)
        assertEquals(2, first.images.amenities.size)
        val second = hotels[1]
        assertNull(second.address)
        assertNull(second.description)
        assertNull(second.amenities)
    }

    @Test
    fun `toDomain maps amenities and fields to domain`() {
        val json =
            """
            {
              "id": "iJhz",
              "destination": 5432,
              "name": "Beach Villas Singapore",
              "lat": 1.264751,
              "lng": 103.824006,
              "address": "8 Sentosa Gateway, Beach Villas, 098269",
              "info": "Located at the western tip of Resorts World Sentosa.",
              "amenities": ["Aircon", "Tv", "Coffee machine", "Kettle", "Hair dryer", "Iron", "Tub"],
              "images": {
                "rooms": [{"url": "https://example.com/1.jpg", "description": "Double room"}],
                "amenities": [{"url": "https://example.com/2.jpg", "description": "Pool"}]
              }
            }
            """.trimIndent()

        val domain = Json.decodeFromString<Hotel>(json).toDomain()

        assertEquals("iJhz", domain.id)
        assertEquals(1.264751, domain.latitude)
        assertEquals(103.824006, domain.longitude)
        assertEquals(emptyList(), domain.amenities.general)
        assertEquals(
            listOf(
                Amenity.AIR_CONDITIONING,
                Amenity.TV,
                Amenity.COFFEE_MACHINE,
                Amenity.KETTLE,
                Amenity.HAIR_DRYER,
                Amenity.IRON,
                Amenity.BATHTUB,
            ),
            domain.amenities.room,
        )
        assertEquals(2, domain.images.rooms.size + domain.images.amenities.size)
        assertEquals(emptyList(), domain.bookingConditions)
    }
}
