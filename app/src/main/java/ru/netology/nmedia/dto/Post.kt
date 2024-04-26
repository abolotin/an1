package ru.netology.nmedia.dto

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val videoUrl: String = "",
    val published: Long,
    val likes: Long = 0,
    val sharesCount: Long = 0,
    val viewsCount: Long = 0,
    val likedByMe: Boolean = false,
)
