package ru.netology.nmedia.domain

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApiImpl
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.entity.PhotoModel
import ru.netology.nmedia.entity.SignUpState
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError
import java.io.File

class SignUpViewModel() : ViewModel() {
    val _state = MutableLiveData<SignUpState>()
    val state: LiveData<SignUpState>
        get() = _state
    private val emptyPhoto = PhotoModel()
    val _photo = MutableLiveData(emptyPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun signUp(login: String, password: String, name: String) {
        _state.postValue(SignUpState(status = SignUpState.Status.SIGNING_UP))

        viewModelScope.launch {
            try {
                println("-------------- PHOTO: " + photo.value?.file);
                val response = PostsApiImpl.retrofitService.signUp(
                    login.toRequestBody("text/plain".toMediaType()),
                    password.toRequestBody("text/plain".toMediaType()),
                    name.toRequestBody("text/plain".toMediaType()),
                    if (photo.value?.file != null && photo.value?.file != emptyPhoto.file) {
                        MultipartBody.Part.createFormData(
                            "file",
                            photo.value?.file?.name ?: "",
                            photo.value?.file?.asRequestBody() ?: "".toRequestBody()
                        )
                    } else null
                )

                if (!response.isSuccessful)
                    throw ApiError(response.code(), response.message())
                val token = response.body() ?: throw ApiError(response.code(), "Empty response")
                AppAuth.getInstance().setAuth(token.id, token.token)
                _state.postValue(SignUpState(status = SignUpState.Status.SUCCESS))
            } catch (e: ApiError) {
                _state.postValue(
                    SignUpState(
                        status = SignUpState.Status.ERROR,
                        errorMessage = e.message ?: "API error"
                    )
                )
            } catch (e: NetworkError) {
                _state.postValue(
                    SignUpState(
                        status = SignUpState.Status.ERROR,
                        errorMessage = e.message ?: "Network Error"
                    )
                )
            } catch (e: Exception) {
                _state.postValue(
                    SignUpState(
                        status = SignUpState.Status.ERROR,
                        errorMessage = e.message ?: "Unknown Error"
                    )
                )
            }
        }
    }

    fun setError(message: String) {
        _state.postValue(SignUpState(status = SignUpState.Status.ERROR, errorMessage = message))
    }

    fun clearState() {
        _state.postValue(SignUpState())
    }

    fun updatePhoto(uri: Uri?, file: File?) {
        _photo.postValue(PhotoModel(uri, file))
    }
}
