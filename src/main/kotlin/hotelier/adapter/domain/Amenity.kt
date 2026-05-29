package com.byron.hotelier.adapter.domain

enum class AmenityCategory { GENERAL, ROOM }

enum class Amenity(val category: AmenityCategory, val patterns: List<Regex>, val displayName: String) {
    // General / hotel-level — more specific entries before broader ones
    INDOOR_POOL(AmenityCategory.GENERAL, listOf(Regex("indoor.?pool")), "indoor pool"),
    OUTDOOR_POOL(AmenityCategory.GENERAL, listOf(Regex("\\bpool\\b"), Regex("swimming"), Regex("outdoor.?pool")), "outdoor pool"),
    BUSINESS_CENTER(AmenityCategory.GENERAL, listOf(Regex("business.?cent")), "business center"),
    WIFI(AmenityCategory.GENERAL, listOf(Regex("wi.?fi"), Regex("\\bwireless\\b")), "wifi"),
    DRY_CLEANING(AmenityCategory.GENERAL, listOf(Regex("dry.?clean")), "dry cleaning"),
    BREAKFAST(AmenityCategory.GENERAL, listOf(Regex("\\bbreakfast\\b")), "breakfast"),
    BAR(AmenityCategory.GENERAL, listOf(Regex("\\bbar\\b")), "bar"),
    CHILDCARE(AmenityCategory.GENERAL, listOf(Regex("child.?care")), "childcare"),
    PARKING(AmenityCategory.GENERAL, listOf(Regex("\\bparking\\b")), "parking"),
    CONCIERGE(AmenityCategory.GENERAL, listOf(Regex("\\bconcierge\\b")), "concierge"),

    // Room-level
    AIR_CONDITIONING(
        AmenityCategory.ROOM,
        listOf(Regex("aircond"), Regex("\\baircon\\b"), Regex("air.?condition"), Regex("\\bac\\b")),
        "aircon",
    ),
    TV(AmenityCategory.ROOM, listOf(Regex("\\btv\\b"), Regex("\\btelevision\\b")), "tv"),
    COFFEE_MACHINE(AmenityCategory.ROOM, listOf(Regex("coffee")), "coffee machine"),
    KETTLE(AmenityCategory.ROOM, listOf(Regex("\\bkettle\\b")), "kettle"),
    HAIR_DRYER(AmenityCategory.ROOM, listOf(Regex("hair.?dry")), "hair dryer"),
    IRON(AmenityCategory.ROOM, listOf(Regex("\\biron\\b")), "iron"),
    BATHTUB(AmenityCategory.ROOM, listOf(Regex("bath.?tub"), Regex("\\btub\\b")), "bathtub"),
    MINIBAR(AmenityCategory.ROOM, listOf(Regex("mini.?bar")), "minibar"),
}

internal fun String.normalise(): String =
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
