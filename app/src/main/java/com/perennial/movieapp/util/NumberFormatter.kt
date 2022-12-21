package com.perennial.movieapp.util

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

object NumberFormatter {

    fun formatSuffix(value: Int): String {
        val valueFloat = value.toFloat()
        if (value < 1000) return value.toString()
        val exp = floor(ln(valueFloat) / ln(1000f)).toInt()
        val truncatedValue = valueFloat / 1000f.pow(exp)
        val suffix = Units.values()[exp - 1].suffix
        return String.format("%.1f%s", truncatedValue, suffix)
    }

    enum class Units(val suffix: String) {
        K("k"),
        M("m"),
        B("b")
    }
}