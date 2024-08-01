package ru.netology.nmedia.errors

sealed class AppErrors(var code: String) : RuntimeException()

class ApiError(val status: Int, code: String) : AppErrors(code)
object NetworkError : AppErrors("network_error")
object UnknownError : AppErrors("unknown_error")