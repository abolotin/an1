package ru.netology.nmedia.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.activity.SingleLiveEvent
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedModel
import ru.netology.nmedia.entity.FeedState
import ru.netology.nmedia.entity.PostEditState
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.repository.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )

    val data = repository.data.map { FeedModel(posts = it) }
    val _dataState = MutableLiveData(FeedState())
    val dataState: LiveData<FeedState>
        get() = _dataState

    private val _postSaved = SingleLiveEvent<Post>()

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
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            } catch (e: Exception) {
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
                repository.save(post);
                _dataState.postValue(FeedState(status = FeedState.Status.READY))
            } catch (e: NetworkError) {
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            } catch (e: Exception) {
                _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
            }
        }
    }

    fun getNewPost() = Post(
        id = 0,
        content = "",
        author = "Me",
        published = 0
    )

    private fun onError(e: Exception, postId: Long?) {
        postId?.let { post ->
            getById(postId)?.let {
                posts = posts.map { if (it.id == postId) it.copy() else it }
            }
        }
        _dataState.postValue(FeedState(status = FeedState.Status.ERROR))
    }

    fun updatePostItem(post: Post) {
        if (getById(post.id) == null) {
            loadPosts()
        } else {
            posts = posts.map { if (it.id == post.id) post.copy() else it }
            _dataState.postValue(FeedState(status = FeedState.Status.READY))
        }
    }
}