package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likesCount: Long = 0,
    val sharesCount: Long = 0,
    val viewsCount: Long = 0,
    val likedByMe: Boolean = false,
)
