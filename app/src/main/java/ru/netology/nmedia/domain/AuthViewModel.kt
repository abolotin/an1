package ru.netology.nmedia.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth : AppAuth
) : ViewModel() {
    val auth: LiveData<Token?> = appAuth.state.asLiveData()

    val isAuthorized: Boolean
        get() = auth.value?.token != null
}