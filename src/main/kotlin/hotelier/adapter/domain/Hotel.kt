package com.byron.hotelier.adapter.domain

data class Image(
    val url: String,
    val description: String,
)

data class Images(
    val rooms: List<Image>,
    val amenities: List<Image>,
    val site: List<Image>,
)

data class Amenities(
    val general: List<String>,
    val room: List<String>,
)

data class Hotel(
    val id: String,
    val destinationId: Int,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val city: String?,
    val country: String?,
    val postalCode: String?,
    val description: String?,
    val amenities: Amenities,
    val images: Images,
    val bookingConditions: List<String>,
)
