package ru.netology.nmedia.entity

import ru.netology.nmedia.dto.Post

data class FeedState(
    val posts: List<Post> = emptyList(),
    val status: Status = Status.READY
) {
    enum class Status {
        READY,
        LOADING,
        ERROR
    }

    val isLoading get() = status == Status.LOADING
    val isError get() = status == Status.ERROR
    val isReady get() = status == Status.READY
    val isEmpty get() = posts.isEmpty() && (status == Status.READY)
}
