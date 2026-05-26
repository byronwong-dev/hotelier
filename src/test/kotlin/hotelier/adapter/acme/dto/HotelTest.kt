package com.byron.hotelier.adapter.acme.dto

import com.byron.hotelier.adapter.domain.Amenity
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HotelTest {
    @Test
    fun `test serialization`() {
        val json =
            """
            [
              {
                "Id": "iJhz",
                "DestinationId": 5432,
                "Name": "Beach Villas Singapore",
                "Latitude": 1.264751,
                "Longitude": 103.824006,
                "Address": " 8 Sentosa Gateway, Beach Villas ",
                "City": "Singapore",
                "Country": "SG",
                "PostalCode": "098269",
                "Description": "  This 5 star hotel is located on the coastline of Singapore.",
                "Facilities": [
                  "Pool",
                  "BusinessCenter",
                  "WiFi ",
                  "DryCleaning",
                  " Breakfast"
                ]
              },
              {
                "Id": "SjyX",
                "DestinationId": 5432,
                "Name": "InterContinental Singapore Robertson Quay",
                "Latitude": null,
                "Longitude": null,
                "Address": " 1 Nanson Road",
                "City": "Singapore",
                "Country": "SG",
                "PostalCode": "238909",
                "Description": "Enjoy sophisticated waterfront living at the new InterContinental® Singapore Robertson Quay, luxury's preferred address nestled in the heart of Robertson Quay along the Singapore River, with the CBD just five minutes drive away. Magnifying the comforts of home, each of our 225 studios and suites features a host of thoughtful amenities that combine modernity with elegance, whilst maintaining functional practicality. The hotel also features a chic, luxurious Club InterContinental Lounge.",
                "Facilities": [
                  "Pool",
                  "WiFi ",
                  "Aircon",
                  "BusinessCenter",
                  "BathTub",
                  "Breakfast",
                  "DryCleaning",
                  "Bar"
                ]
              },
              {
                "Id": "f8c9",
                "DestinationId": 1122,
                "Name": "Hilton Shinjuku Tokyo",
                "Latitude": "",
                "Longitude": "",
                "Address": "160-0023, SHINJUKU-KU, 6-6-2 NISHI-SHINJUKU, JAPAN",
                "City": "Tokyo",
                "Country": "JP",
                "PostalCode": "160-0023",
                "Description": "Hilton Tokyo is located in Shinjuku, the heart of Tokyo's business, shopping and entertainment district, and is an ideal place to experience modern Japan. A complimentary shuttle operates between the hotel and Shinjuku station and the Tokyo Metro subway is connected to the hotel. Relax in one of the modern Japanese-style rooms and admire stunning city views. The hotel offers WiFi and internet access throughout all rooms and public space.",
                "Facilities": [
                  "Pool",
                  "WiFi ",
                  "BusinessCenter",
                  "DryCleaning",
                  " Breakfast",
                  "Bar",
                  "BathTub"
                ]
              }
            ]
            """.trimIndent()

        val decoded = Json.decodeFromString<List<Hotel>>(json)
        assertEquals(3, decoded.size)

        val h1 = decoded[0]
        assertEquals("iJhz", h1.id)
        assertEquals(5432, h1.destinationId)
        assertEquals("Beach Villas Singapore", h1.name)
        assertEquals(1.264751, h1.latitude)
        assertEquals(103.824006, h1.longitude)
        assertEquals("8 Sentosa Gateway, Beach Villas", h1.address)
        assertEquals("Singapore", h1.city)
        assertEquals("SG", h1.country)
        assertEquals("098269", h1.postalCode)
        assertEquals("This 5 star hotel is located on the coastline of Singapore.", h1.description)
        assertEquals(listOf("Pool", "BusinessCenter", "WiFi", "DryCleaning", "Breakfast"), h1.facilities)

        val h2 = decoded[1]
        assertEquals("SjyX", h2.id)
        assertEquals(5432, h2.destinationId)
        assertEquals("InterContinental Singapore Robertson Quay", h2.name)
        assertNull(h2.latitude)
        assertNull(h2.longitude)
        assertEquals("1 Nanson Road", h2.address)
        assertEquals("Singapore", h2.city)
        assertEquals("SG", h2.country)
        assertEquals("238909", h2.postalCode)
        assertEquals(listOf("Pool", "WiFi", "Aircon", "BusinessCenter", "BathTub", "Breakfast", "DryCleaning", "Bar"), h2.facilities)

        val h3 = decoded[2]
        assertEquals("f8c9", h3.id)
        assertEquals(1122, h3.destinationId)
        assertEquals("Hilton Shinjuku Tokyo", h3.name)
        assertNull(h3.latitude)
        assertNull(h3.longitude)
        assertEquals("160-0023, SHINJUKU-KU, 6-6-2 NISHI-SHINJUKU, JAPAN", h3.address)
        assertEquals("Tokyo", h3.city)
        assertEquals("JP", h3.country)
        assertEquals("160-0023", h3.postalCode)
        assertEquals(listOf("Pool", "WiFi", "BusinessCenter", "DryCleaning", "Breakfast", "Bar", "BathTub"), h3.facilities)
    }

    @Test
    fun `toDomain maps amenities and fields to domain`() {
        val json =
            """
            {
              "Id": "iJhz",
              "DestinationId": 5432,
              "Name": "Beach Villas Singapore",
              "Latitude": 1.264751,
              "Longitude": 103.824006,
              "Address": "8 Sentosa Gateway, Beach Villas",
              "City": "Singapore",
              "Country": "SG",
              "PostalCode": "098269",
              "Description": "This 5 star hotel is located on the coastline of Singapore.",
              "Facilities": ["Pool", "BusinessCenter", "WiFi ", "DryCleaning", " Breakfast", "Aircon", "BathTub"]
            }
            """.trimIndent()

        val domain = Json.decodeFromString<Hotel>(json).toDomain()

        assertEquals("iJhz", domain.id)
        assertEquals(5432, domain.destinationId)
        assertEquals("Beach Villas Singapore", domain.name)
        assertEquals(1.264751, domain.latitude)
        assertEquals(103.824006, domain.longitude)
        assertEquals("8 Sentosa Gateway, Beach Villas", domain.address)
        assertEquals("Singapore", domain.city)
        assertEquals("SG", domain.country)
        assertEquals("098269", domain.postalCode)
        assertEquals(
            listOf(Amenity.OUTDOOR_POOL, Amenity.BUSINESS_CENTER, Amenity.WIFI, Amenity.DRY_CLEANING, Amenity.BREAKFAST),
            domain.amenities.general,
        )
        assertEquals(listOf(Amenity.AIR_CONDITIONING, Amenity.BATHTUB), domain.amenities.room)
    }
}
