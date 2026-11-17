package com.quijada.campusgps.util

import java.util.Locale
import kotlin.math.roundToInt

fun formatearDistancia(metros: Float): String {
    return if (metros < 1000f) {
        "${metros.roundToInt()} m"
    } else {
        String.format(Locale.US, "%.1f km", metros / 1000f)
    }
}
