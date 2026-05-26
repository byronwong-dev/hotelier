package com.byron.hotelier.adapter.paperflies.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val address: String,
    val country: String,
)

@Serializable
data class Amenities(
    val general: List<String>,
    val room: List<String>,
)

@Serializable
data class Image(
    val link: String,
    val caption: String,
)

@Serializable
data class Images(
    val rooms: List<Image>,
    val site: List<Image>,
)

@Serializable
data class Hotel(
    @SerialName("hotel_id") val id: String,
    @SerialName("destination_id") val destinationId: Int,
    @SerialName("hotel_name") val name: String,
    val location: Location,
    val details: String,
    val amenities: Amenities,
    val images: Images,
    @SerialName("booking_conditions") val bookingConditions: List<String>,
)
