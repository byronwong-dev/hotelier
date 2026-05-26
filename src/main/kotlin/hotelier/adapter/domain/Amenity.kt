package com.byron.hotelier.adapter.domain

enum class AmenityCategory { GENERAL, ROOM }

enum class Amenity(val category: AmenityCategory, val patterns: List<Regex>) {
    // General / hotel-level — more specific entries before broader ones
    INDOOR_POOL(AmenityCategory.GENERAL, listOf(Regex("indoor.?pool"))),
    OUTDOOR_POOL(AmenityCategory.GENERAL, listOf(Regex("\\bpool\\b"), Regex("swimming"), Regex("outdoor.?pool"))),
    BUSINESS_CENTER(AmenityCategory.GENERAL, listOf(Regex("business.?cent"))),
    WIFI(AmenityCategory.GENERAL, listOf(Regex("wi.?fi"), Regex("\\bwireless\\b"))),
    DRY_CLEANING(AmenityCategory.GENERAL, listOf(Regex("dry.?clean"))),
    BREAKFAST(AmenityCategory.GENERAL, listOf(Regex("\\bbreakfast\\b"))),
    BAR(AmenityCategory.GENERAL, listOf(Regex("\\bbar\\b"))),
    CHILDCARE(AmenityCategory.GENERAL, listOf(Regex("child.?care"))),
    PARKING(AmenityCategory.GENERAL, listOf(Regex("\\bparking\\b"))),
    CONCIERGE(AmenityCategory.GENERAL, listOf(Regex("\\bconcierge\\b"))),

    // Room-level
    AIR_CONDITIONING(AmenityCategory.ROOM, listOf(Regex("aircond"), Regex("\\baircon\\b"), Regex("air.?condition"), Regex("\\bac\\b"))),
    TV(AmenityCategory.ROOM, listOf(Regex("\\btv\\b"), Regex("\\btelevision\\b"))),
    COFFEE_MACHINE(AmenityCategory.ROOM, listOf(Regex("coffee"))),
    KETTLE(AmenityCategory.ROOM, listOf(Regex("\\bkettle\\b"))),
    HAIR_DRYER(AmenityCategory.ROOM, listOf(Regex("hair.?dry"))),
    IRON(AmenityCategory.ROOM, listOf(Regex("\\biron\\b"))),
    BATHTUB(AmenityCategory.ROOM, listOf(Regex("bath.?tub"), Regex("\\btub\\b"))),
    MINIBAR(AmenityCategory.ROOM, listOf(Regex("mini.?bar"))),
}

private fun String.normalise(): String =
    trim().lowercase()
        .replace(Regex("[^a-z0-9 ]"), "")
        .replace(Regex(" +"), " ")
        .trim()

fun String.toAmenity(): Amenity? {
    val normalised = normalise()
    return Amenity.entries.firstOrNull { amenity ->
        amenity.patterns.any { it.containsMatchIn(normalised) }
    }
}

fun List<String>.toAmenities(): Amenities {
    val mapped = mapNotNull { it.toAmenity() }
    return Amenities(
        general = mapped.filter { it.category == AmenityCategory.GENERAL },
        room = mapped.filter { it.category == AmenityCategory.ROOM },
    )
}
