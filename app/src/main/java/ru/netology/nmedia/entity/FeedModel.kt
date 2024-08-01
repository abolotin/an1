package ru.netology.nmedia.entity

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
) {
    val isEmpty: Boolean
        get() = posts.isEmpty()
}