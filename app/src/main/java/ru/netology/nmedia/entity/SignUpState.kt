package ru.netology.nmedia.entity

data class SignUpState(
    val status: Status = Status.READY,
    val errorMessage: String = ""
) {
    enum class Status {
        READY,
        SUCCESS,
        SIGNING_UP,
        ERROR
    }

    val isReady get() = status == Status.READY
    val isSigningUp get() = status == Status.SIGNING_UP
    val isError get() = status == Status.ERROR
    val isSuccess get() = status == Status.SUCCESS
}
