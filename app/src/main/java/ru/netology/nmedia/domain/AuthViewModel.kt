package ru.netology.nmedia.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token

class AuthViewModel : ViewModel() {
    val auth: LiveData<Token?> = AppAuth.getInstance().state.asLiveData()

    val isAuthorized: Boolean
        get() = auth.value?.token != null
}