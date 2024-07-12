package ru.netology.nmedia.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.callbackFlow
import ru.netology.nmedia.activity.SingleLiveEvent
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedState
import ru.netology.nmedia.entity.PostEditState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRetrofitImpl
import kotlin.concurrent.thread

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRetrofitImpl()
    private val _feedState = MutableLiveData(FeedState())
    private val _postSaved = SingleLiveEvent<Post>()
    private val updatePostCallback = object: PostRepository.GetPostCallback {
        override fun onError(e: Exception, postId: Long) = this@PostViewModel.onError(e, postId)
        override fun onSuccess(loadedPost: Post) = updatePostItem(loadedPost)
    }

    val editState = MutableLiveData(PostEditState(status = PostEditState.Status.OK))
    val postSaved: LiveData<Post> get() = _postSaved

    var editedPost = getNewPost()
    private var posts: List<Post> = emptyList()

    val feedState: LiveData<FeedState>
        get() = _feedState

    init {
        loadPosts()
    }

    fun loadPosts() {
        _feedState.postValue(FeedState(status = FeedState.Status.LOADING))
        repository.getAllAsync(object : PostRepository.GetPostsCallback {
            override fun onError(e: Exception) = this@PostViewModel.onError(e, null)

            override fun onSuccess(loadedPosts: List<Post>) {
                posts = loadedPosts
                _feedState.postValue(FeedState(posts = posts, status = FeedState.Status.READY))
            }
        })
    }

    fun getById(id: Long): Post? = posts.filter { id == it.id }.firstOrNull()

    fun updateLike(id: Long, likedByMe: Boolean) {
        if (likedByMe)
            repository.unlikeByMeAsync(id, updatePostCallback)
        else
            repository.likeByMeAsync(id, updatePostCallback)
    }

    fun shareById(id: Long) {
        getById(id)?.let {
            repository.saveAsync(
                it.copy(sharesCount = it.sharesCount+1),
                updatePostCallback
            )
        }
    }

    fun removeById(id: Long) {
        _feedState.postValue(FeedState(status = FeedState.Status.LOADING))
        repository.removeByIdAsync(id, object: PostRepository.RemovePostCallback {
            override fun onError(e: Exception) {
                _feedState.postValue(FeedState(status = FeedState.Status.ERROR))
            }

            override fun onSuccess(id: Long) {
                posts = posts.filter { it.id != id }
                _feedState.postValue(FeedState(posts = posts, status = FeedState.Status.READY))
            }
        })
    }

    fun savePost(post: Post) {
        editState.postValue(PostEditState(status = PostEditState.Status.SAVING))
        repository.saveAsync(post, object: PostRepository.GetPostCallback {
            override fun onError(e: Exception, postId: Long) {
                editState.postValue(PostEditState(status = PostEditState.Status.ERROR))
            }
            override fun onSuccess(loadedPost: Post) {
                _postSaved.postValue(loadedPost)
                editState.postValue(PostEditState(status = PostEditState.Status.OK))
            }
        })
    }

    fun getNewPost() = Post(
        id = 0,
        content = "",
        author = "Me",
        published = 0
    )

    private fun onError(e: Exception, postId: Long?) {
        postId?.let{post ->
            getById(postId)?.let {
                posts = posts.map { if (it.id == postId) it.copy() else it }
            }
        }
        _feedState.postValue(FeedState(posts = posts, status = FeedState.Status.ERROR))
    }

    fun updatePostItem(post: Post) {
        if (getById(post.id) == null) {
            loadPosts()
        } else {
            posts = posts.map { if (it.id == post.id) post.copy() else it }
            _feedState.postValue(FeedState(posts = posts, status = FeedState.Status.READY))
        }
    }
}