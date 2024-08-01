package ru.netology.nmedia.entity

data class FeedState(
    val status: Status = Status.READY
) {
    enum class Status {
        READY,
        LOADING,
        ERROR
    }

    val isReady get() = status == Status.READY
    val isLoading get() = status == Status.LOADING
    val isError get() = status == Status.ERROR
}
