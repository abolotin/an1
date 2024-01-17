package ru.netology.nmedia.dto

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.ln
import kotlin.math.pow

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likesCount: Long = 0,
    var sharesCount: Long = 0,
    var viewsCount: Long = 0,
    var likedByMe: Boolean = false
) {

    fun getLikesCountText(): String {
        return numberToString(likesCount);
    }

    fun getSharesCountText(): String {
        return numberToString(sharesCount);
    }

    fun getViewsCountText(): String {
        return numberToString(viewsCount);
    }

    fun numberToString(count: Long): String {
        if (count < 1000) return count.toString();
        val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()

        println(count.toDouble().toString() + ", " + exp.toString());
        val df = DecimalFormat("###.#");
        df.roundingMode = RoundingMode.FLOOR
        return String.format(
            "%s%c",
            df.format(count / 1000.0.pow(exp.toDouble())),
            "kMGTPE"[exp - 1]
        )
    }
}
