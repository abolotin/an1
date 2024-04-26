package ru.netology.nmedia.entity

data class PostEditState(
    val status: Status = Status.OK
) {
    enum class Status {
        OK,
        SAVING,
        ERROR
    }

    val isSaving get() = status == Status.SAVING
    val isError get() = status == Status.ERROR

    val isOk get() = status == Status.OK
}
