package com.byron.hotelier.adapter.patagonia.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val url: String,
    val description: String,
)

@Serializable
data class Images(
    val rooms: List<Image>,
    val amenities: List<Image>,
)

@Serializable
data class Hotel(
    val id: String,
    @SerialName("destination") val destinationId: Int,
    val name: String,
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double,
    val address: String?,
    @SerialName("info") val description: String?,
    val amenities: List<String>?,
    val images: Images,
)
