package com.byron.hotelier.lib

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
val PascalCaseNamingStrategy = JsonNamingStrategy { _, _, serialName ->
    serialName.replaceFirstChar { it.uppercase() }
}

@OptIn(ExperimentalSerializationApi::class)
val PascalCaseJson = Json {
    namingStrategy = PascalCaseNamingStrategy
}
