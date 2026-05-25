package com.byron.hotelier.adapter.acme.dto

import com.byron.hotelier.lib.PascalCaseJson
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class HotelTest {
    @Test
    fun `test serialization`() =
        testApplication {
            val json = """
[{
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
  }]
            """.trimIndent()
            val format = Json(PascalCaseJson) { isLenient = true }
            val decoded = format.decodeFromString<List<Hotel>>(json)
            assertEquals(1, decoded.size)
            assertEquals("iJhz", decoded.first().id)
        }
}