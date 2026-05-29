package com.byron.hotelier.controller

import com.byron.hotelier.adapter.domain.Hotel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.byron.hotelier.adapter.acme.dto.Hotel as AcmeHotel
import com.byron.hotelier.adapter.paperflies.dto.Hotel as PaperfliesHotel
import com.byron.hotelier.adapter.patagonia.dto.Hotel as PatagoniaHotel

enum class HotelSupplier(val fetcher: suspend HttpClient.() -> List<Hotel>) {
    ACME({
        get("https://5f2be0b4ffc88500167b85a0.mockapi.io/suppliers/acme")
            .body<List<AcmeHotel>>()
            .map { it.toDomain() }
    }),
    PAPERFLIES({
        get("https://5f2be0b4ffc88500167b85a0.mockapi.io/suppliers/paperflies")
            .body<List<PaperfliesHotel>>()
            .map { it.toDomain() }
    }),
    PATAGONIA({
        get("https://5f2be0b4ffc88500167b85a0.mockapi.io/suppliers/patagonia")
            .body<List<PatagoniaHotel>>()
            .map { it.toDomain() }
    }),
}
