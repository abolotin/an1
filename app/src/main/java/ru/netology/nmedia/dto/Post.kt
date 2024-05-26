package ru.netology.nmedia.dto

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val videoUrl: String = "",
    val published: Long,
    var likes: Long = 0,
    var sharesCount: Long = 0,
    var viewsCount: Long = 0,
    var likedByMe: Boolean = false,
)
