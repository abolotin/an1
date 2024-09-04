package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow<Token?>(null)
    val state: StateFlow<Token?> = _state.asStateFlow()
    val isAuthorized : Boolean
        get() = _state.value != null

    init {
        val id = prefs.getLong(ID_KEY, 0L)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id != 0L && token != null) {
            _state.value = Token(id, token)
        } else {
            prefs.edit { clear() }
        }
    }

    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
            apply()
        }
        _state.value = Token(id, token)
    }

    fun clearAuth() {
        prefs.edit {
            clear()
            apply()
        }
        _state.value = null
    }

    companion object {
        private val ID_KEY = "ID_KEY"
        private val TOKEN_KEY = "TOKEN_KEY"

        private var INSTANCE: AppAuth? = null

        fun getInstance() = requireNotNull(INSTANCE) {
            "You must call init before"
        }

        fun init(context: Context) {
            INSTANCE = AppAuth(context.applicationContext)
        }
    }
}