package ru.netology.nmedia.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.netology.nmedia.api.PostsApiImpl
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.entity.SignInState
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError

class SignInViewModel() : ViewModel() {
    val _state = MutableLiveData<SignInState>()
    val state : LiveData<SignInState>
        get() = _state

    fun signIn(login: String, password: String) {
        _state.postValue(SignInState(status = SignInState.Status.SIGNING_IN))

        viewModelScope.launch {
            try {
                val response = PostsApiImpl.retrofitService.signIn(
                    MultipartBody.Part.createFormData(
                        "login",
                        login,
                    ),
                    MultipartBody.Part.createFormData(
                        "pass",
                        password,
                    )
                )
                if (!response.isSuccessful)
                    throw ApiError(response.code(), response.message())
                val token = response.body() ?: throw ApiError(response.code(), "Empty response")
                AppAuth.getInstance().setAuth(token.id, token.token)
                _state.postValue(SignInState(status = SignInState.Status.SUCCESS))
            } catch (e: ApiError) {
                _state.postValue(SignInState(status = SignInState.Status.ERROR, errorMessage = e.message ?: "API error"))
            } catch (e: NetworkError) {
                _state.postValue(SignInState(status = SignInState.Status.ERROR, errorMessage = e.message ?: "Network Error"))
            } catch (e: Exception) {
                _state.postValue(SignInState(status = SignInState.Status.ERROR, errorMessage = e.message ?: "Unknown Error"))
            }
        }
    }

    fun setError(message: String) {
        _state.postValue(SignInState(status = SignInState.Status.ERROR, errorMessage = message))
    }

    fun clearState() {
        _state.postValue(SignInState())
    }
}