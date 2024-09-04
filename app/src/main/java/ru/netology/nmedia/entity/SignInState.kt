package ru.netology.nmedia.entity

data class SignInState(
    val status: Status = Status.READY,
    val errorMessage: String = ""
) {
    enum class Status {
        READY,
        SUCCESS,
        SIGNING_IN,
        ERROR
    }

    val isReady get() = status == Status.READY
    val isSigningIn get() = status == Status.SIGNING_IN
    val isError get() = status == Status.ERROR
    val isSuccess get() = status == Status.SUCCESS
}
