package ru.netology.nmedia.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Date
import kotlin.math.ln
import kotlin.math.pow

fun numberToString(number: Long): String {
    val df: DecimalFormat
    when (number) {
        in 0L..999L -> return number.toString()
        in 10_000L..999_999L -> df = DecimalFormat("###")
        else -> df = DecimalFormat("###.#")
    }

    val exp = (ln(number.toDouble()) / ln(1000.0)).toInt()

    df.roundingMode = RoundingMode.FLOOR
    return String.format(
        "%s%c",
        df.format(number / 1000.0.pow(exp.toDouble())),
        "kMGTPE"[exp - 1]
    )
}

fun timeToFeedSeparatorText(unixtime: Long): String {
    val currentTime = Date()
    if ((currentTime.time/1000L - unixtime) < 86400) return "Сегодня"
    if ((currentTime.time/1000L - unixtime) < 2*86400) return "Вчера"
    return "На прошлой неделе"
}