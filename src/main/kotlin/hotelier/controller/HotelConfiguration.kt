package com.byron.hotelier.controller

import io.ktor.server.config.ApplicationConfig

class HotelConfiguration(config: ApplicationConfig) {
    val enabledSuppliers: List<HotelSupplier> =
        config.propertyOrNull("hotelier.suppliers.enabled")
            ?.getList()
            ?.mapNotNull { name ->
                HotelSupplier.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
            }
            ?: emptyList() // defensive, not showing > showing wrong value

    val maxRetries: Int =
        config.propertyOrNull("hotelier.suppliers.maxRetries")
            ?.getString()
            ?.toIntOrNull()
            ?: 3
}
