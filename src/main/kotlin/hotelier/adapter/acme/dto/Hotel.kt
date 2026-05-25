package com.byron.hotelier.adapter.acme.dto

import kotlinx.serialization.Serializable

@Serializable
data class Hotel(
    val id: String,
    val destinationId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val city: String,
    val country: String,
    val postalCode: String,
    val description: String,
    val facilities: List<String>
)
