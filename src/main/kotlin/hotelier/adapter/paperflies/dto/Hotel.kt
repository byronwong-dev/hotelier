package com.byron.hotelier.adapter.paperflies.dto

import com.byron.hotelier.adapter.domain.toAmenities
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.byron.hotelier.adapter.domain.Hotel as DomainHotel
import com.byron.hotelier.adapter.domain.Image as DomainImage
import com.byron.hotelier.adapter.domain.Images as DomainImages

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
) {
    fun toDomain() =
        DomainHotel(
            id = id,
            destinationId = destinationId,
            name = name,
            latitude = null,
            longitude = null,
            address = location.address,
            city = null,
            country = location.country,
            postalCode = null,
            description = details,
            amenities = (amenities.general + amenities.room).toAmenities(),
            images =
                DomainImages(
                    rooms = images.rooms.map { DomainImage(url = it.link, description = it.caption) },
                    amenities = emptyList(),
                    site = images.site.map { DomainImage(url = it.link, description = it.caption) },
                ),
            bookingConditions = bookingConditions,
        )
}
