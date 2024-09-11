package ru.netology.nmedia.domain

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.activity.SingleLiveEvent
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedModel
import ru.netology.nmedia.entity.FeedState
import ru.netology.nmedia.entity.PhotoModel
import ru.netology.nmedia.entity.PostEditState
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.repository.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {
    /* private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    ) */

    val data = repository.data
        .debounce(200L)
        .map(::FeedModel)
        .catch { e ->
            e.printStackTrace()
        }
        .asLiveData(Dispatchers.Default)
    private val emptyPhoto = PhotoModel()
    val _photo = MutableLiveData(emptyPhoto)
    val photo : LiveData<PhotoModel>
        get() = _photo

    val _dataState = MutableLiveData(FeedState())
    val dataState: LiveData<FeedState>
        get() = _dataState

    private val _postSaved = SingleLiveEvent<Post>()

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e ->
                e.printStackTrace()
            }
            .asLiveData(Dispatchers.Default)
    }

    val editState = MutableLiveData(PostEditState(status = PostEditState.Status.OK))
    val postSaved: LiveData<Post> get() = _postSaved

    var editedPost = getNewPost()
    private var posts: List<Post> = emptyList()

    init {
        loadPosts()
    }

    fun loadPosts() {
        _dataState.postValue(FeedState(status = FeedState.Status.LOADING))
        viewModelScope.launch {
            try {
                repository.getAll()
                _dataState.postValue(FeedState(status = FeedState.Status.READY))
            } catch (e: NetworkError) {
                e.printStackTrace()
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            } catch (e: Exception) {
                e.printStackTrace()
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            }
        }
    }

    fun getById(id: Long): Post? = posts.filter { id == it.id }.firstOrNull()

    fun updateLike(id: Long, likedByMe: Boolean) {
        viewModelScope.launch {
            try {
                if (likedByMe) repository.unlikeByMe(id) else repository.likeByMe(id)
            } catch (e: NetworkError) {
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            } catch (e: Exception) {
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            }
        }
    }

    fun shareById(id: Long) {
        getById(id)?.let {
            viewModelScope.launch {
                try {
                    repository.save(it.copy(sharesCount = it.sharesCount + 1))
                } catch (e: NetworkError) {
                    _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
                } catch (e: Exception) {
                    _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
                }
            }
        }
    }

    fun removePost(id: Long, localId: Long) {
        _dataState.postValue(FeedState(status = FeedState.Status.LOADING))
        viewModelScope.launch {
            repository.remove(
                id = id,
                localId = localId
            )
            posts = posts.filter { it.id != id }
            _dataState.postValue(FeedState(status = FeedState.Status.READY))
        }
    }

    fun savePost(post: Post) {
        viewModelScope.launch {
            _dataState.postValue(FeedState(status = FeedState.Status.LOADING))
            try {
                repository.save(post, photo.value);
                _dataState.postValue(FeedState(status = FeedState.Status.READY))
            } catch (e: NetworkError) {
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            } catch (e: Exception) {
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            }
        }
    }

    fun updatePhoto(uri: Uri?, file: File?) {
        _photo.postValue(PhotoModel(uri, file))
    }

    fun setAllViewed() {
        viewModelScope.launch {
            repository.setAllViewed()
        }
    }

    fun getNewPost() = Post(
        id = 0,
        content = "",
        author = "Me",
        published = 0,
        authorId = appAuth.state.value?.id ?: 0
    )
}