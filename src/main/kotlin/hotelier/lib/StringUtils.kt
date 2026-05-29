package com.byron.hotelier.lib

fun String?.takeIfNotBlank(fallback: String?): String? = this?.takeIf { it.isNotBlank() } ?: fallback
