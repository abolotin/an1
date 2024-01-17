package ru.netology.nmedia.dto

import ru.netology.nmedia.util.numberToString

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

    fun getLikesCountText() = numberToString(this.likesCount)

    fun getSharesCountText() = numberToString(this.sharesCount)

    fun getViewsCountText() = numberToString(this.viewsCount)
}
