package com.byron.hotelier.adapter.patagonia.dto

import com.byron.hotelier.adapter.domain.toAmenities
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.byron.hotelier.adapter.domain.Hotel as DomainHotel
import com.byron.hotelier.adapter.domain.Image as DomainImage
import com.byron.hotelier.adapter.domain.Images as DomainImages

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
) {
    fun toDomain() =
        DomainHotel(
            id = id,
            destinationId = destinationId,
            name = name,
            latitude = latitude,
            longitude = longitude,
            address = address,
            city = null,
            country = null,
            postalCode = null,
            description = description,
            amenities = (amenities ?: emptyList()).toAmenities(),
            images =
                DomainImages(
                    rooms = images.rooms.map { DomainImage(url = it.url, description = it.description) },
                    amenities = images.amenities.map { DomainImage(url = it.url, description = it.description) },
                    site = emptyList(),
                ),
            bookingConditions = emptyList(),
        )
}
