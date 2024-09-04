package ru.netology.nmedia.domain

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.netology.nmedia.api.PostsApiImpl
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.entity.SignUpState
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError

class SignUpViewModel() : ViewModel() {
    val _state = MutableLiveData<SignUpState>()
    val state : LiveData<SignUpState>
        get() = _state

    fun signUp(login: String, password: String) {
        _state.postValue(SignUpState(status = SignUpState.Status.SIGNING_UP))

        viewModelScope.launch {
            try {
                val response = PostsApiImpl.retrofitService.signUp(
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
                _state.postValue(SignUpState(status = SignUpState.Status.SUCCESS))
            } catch (e: ApiError) {
                _state.postValue(SignUpState(status = SignUpState.Status.ERROR, errorMessage = e.message ?: "API error"))
            } catch (e: NetworkError) {
                _state.postValue(SignUpState(status = SignUpState.Status.ERROR, errorMessage = e.message ?: "Network Error"))
            } catch (e: Exception) {
                _state.postValue(SignUpState(status = SignUpState.Status.ERROR, errorMessage = e.message ?: "Unknown Error"))
            }
        }
    }

    fun setError(message: String) {
        _state.postValue(SignUpState(status = SignUpState.Status.ERROR, errorMessage = message))
    }

    fun clearState() {
        _state.postValue(SignUpState())
    }
}