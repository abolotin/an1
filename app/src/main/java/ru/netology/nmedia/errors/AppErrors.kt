package ru.netology.nmedia.errors

import java.io.IOException
import java.sql.SQLException
import kotlin.jvm.Throws

sealed class AppErrors(var code: String) : RuntimeException() {
    companion object {
        fun from(e: Throwable): AppErrors = when(e) {
            is AppErrors -> e
            is SQLException -> DatabaseError
            is IOException -> NetworkError
            else -> UnknownError
        }
    }
}

class ApiError(val status: Int, code: String) : AppErrors(code)
object NetworkError : AppErrors("network_error")
object UnknownError : AppErrors("unknown_error")
object DatabaseError : AppErrors("database_error")
