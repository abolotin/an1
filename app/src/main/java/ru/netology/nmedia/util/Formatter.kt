package ru.netology.nmedia.util

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow

class Formatter {
    companion object {
        fun numberToString(number: Long) : String {
            if (number < 1000) return number.toString()
            val exp = (ln(number.toDouble()) / ln(1000.0)).toInt()

            println(number.toDouble().toString() + ", " + exp.toString())
            val df = DecimalFormat("###.#")
            df.roundingMode = RoundingMode.FLOOR
            return String.format(
                "%s%c",
                df.format(number / 1000.0.pow(exp.toDouble())),
                "kMGTPE"[exp - 1]
            )
        }
    }
}