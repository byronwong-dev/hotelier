@file:UseSerializers(TrimmedStringSerializer::class)

package com.byron.hotelier.adapter.acme.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double

private object TrimmedStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TrimmedString", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: String,
    ) = encoder.encodeString(value)

    override fun deserialize(decoder: Decoder): String = decoder.decodeString().trim()
}

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
private object EmptyStringAsNullDoubleSerializer : KSerializer<Double?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EmptyStringAsNullDouble", PrimitiveKind.DOUBLE)

    override fun serialize(
        encoder: Encoder,
        value: Double?,
    ) {
        if (value == null) encoder.encodeNull() else encoder.encodeDouble(value)
    }

    override fun deserialize(decoder: Decoder): Double? {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        return when {
            element is JsonNull -> null
            element is JsonPrimitive && element.isString && element.content.isEmpty() -> null
            else -> (element as JsonPrimitive).double
        }
    }
}

@Serializable
data class Hotel(
    @SerialName("Id") val id: String,
    @SerialName("DestinationId") val destinationId: Int,
    @SerialName("Name") val name: String,
    @SerialName("Latitude") @Serializable(with = EmptyStringAsNullDoubleSerializer::class) val latitude: Double?,
    @SerialName("Longitude") @Serializable(with = EmptyStringAsNullDoubleSerializer::class) val longitude: Double?,
    @SerialName("Address") val address: String,
    @SerialName("City") val city: String,
    @SerialName("Country") val country: String,
    @SerialName("PostalCode") val postalCode: String,
    @SerialName("Description") val description: String,
    @SerialName("Facilities") val facilities: List<String>,
)
