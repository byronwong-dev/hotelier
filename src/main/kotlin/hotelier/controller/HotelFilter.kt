package com.byron.hotelier.controller

data class HotelFilter(
    val query: String?,
    val travellers: TravellerPreset?,
)

enum class TravellerPreset(val adults: Int, val children: Int, val label: String) {
    SOLO(1, 0, "1+0"),
    COUPLE(2, 0, "2+0"),
    COUPLE_ONE_CHILD(2, 1, "2+1"),
    COUPLE_TWO_CHILDREN(2, 2, "2+2"),
    GROUP(3, 0, "3+0"),
    FAMILY(3, 2, "3+2"),
    LARGE_GROUP(4, 0, "4+0"),
    LARGE_FAMILY(4, 2, "4+2");

    companion object {
        fun fromLabel(label: String): TravellerPreset? = entries.firstOrNull { it.label == label }
    }
}
