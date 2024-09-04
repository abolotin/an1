package ru.netology.nmedia.dto

data class Token(
    val id: Long,
    val token: String
) {
    val isAuthorized: Boolean
        get() = !token.isEmpty()
}
