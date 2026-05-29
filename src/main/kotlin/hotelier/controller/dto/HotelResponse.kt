package com.byron.hotelier.controller.dto

import com.byron.hotelier.adapter.domain.Hotel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HotelResponse(
    val id: String,
    @SerialName("destination_id") val destinationId: Int,
    val name: String,
    val location: LocationResponse,
    val description: String?,
    val amenities: AmenitiesResponse,
    val images: ImagesResponse,
    @SerialName("booking_conditions") val bookingConditions: List<String>,
)

@Serializable
data class LocationResponse(
    val lat: Double?,
    val lng: Double?,
    val address: String?,
    val city: String?,
    val country: String?,
)

@Serializable
data class AmenitiesResponse(
    val general: List<String>,
    val room: List<String>,
)

@Serializable
data class ImageResponse(
    val link: String,
    val description: String,
)

@Serializable
data class ImagesResponse(
    val rooms: List<ImageResponse>,
    val site: List<ImageResponse>,
    val amenities: List<ImageResponse>,
)

fun Hotel.toResponse() =
    HotelResponse(
        id = id,
        destinationId = destinationId,
        name = name,
        location =
            LocationResponse(
                lat = latitude,
                lng = longitude,
                address = address,
                city = city,
                country = country,
            ),
        description = description,
        amenities =
            AmenitiesResponse(
                general = amenities.general.map { it.displayName },
                room = amenities.room.map { it.displayName },
            ),
        images =
            ImagesResponse(
                rooms = images.rooms.map { ImageResponse(it.url, it.description) },
                site = images.site.map { ImageResponse(it.url, it.description) },
                amenities = images.amenities.map { ImageResponse(it.url, it.description) },
            ),
        bookingConditions = bookingConditions,
    )
