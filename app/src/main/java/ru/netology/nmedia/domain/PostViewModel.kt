package ru.netology.nmedia.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.activity.SingleLiveEvent
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedState
import ru.netology.nmedia.entity.PostEditState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryNetImpl
import kotlin.concurrent.thread

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryNetImpl()
    private val _feedState = MutableLiveData(FeedState())
    private val _postCreated = SingleLiveEvent<Unit>()
    val editState = MutableLiveData(PostEditState(status = PostEditState.Status.OK))
    val postCreated: LiveData<Unit> get() = _postCreated

    var editedPost = getNewPost()
    private var posts : List<Post> = emptyList()

    val feedState: LiveData<FeedState>
        get() = _feedState

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _feedState.postValue(FeedState(status = FeedState.Status.LOADING))
            try {
                posts = repository.getAll()
                FeedState(posts = posts, status = FeedState.Status.READY)
            } catch (e: Exception) {
                FeedState(status = FeedState.Status.ERROR)
            }.also(_feedState::postValue)
        }
    }

    fun getById(id: Long): Post? = repository.getById(id)

    fun updateLike(id: Long, likedByMe: Boolean) {
        thread {
            _feedState.postValue(FeedState(status = FeedState.Status.LOADING))
            try {
                val post = if (likedByMe) repository.unlikeByMe(id) else repository.likeByMe(id)
                posts.find { it.id == post.id }?.let {
                    it.likedByMe = post.likedByMe
                    it.likes = post.likes
                }
                FeedState(posts = posts, status = FeedState.Status.READY)
            } catch (e: Exception) {
                FeedState(status = FeedState.Status.ERROR)
            }.also(_feedState::postValue)
        }
    }

    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long) {
        thread {
            _feedState.postValue(FeedState(status = FeedState.Status.LOADING))
            try {
                repository.removeById(id)
                posts = posts.filter { it.id != id }
                FeedState(posts = posts, status = FeedState.Status.READY)
            } catch (e: Exception) {
                FeedState(status = FeedState.Status.ERROR)
            }.also(_feedState::postValue)
        }
    }

    fun savePost(post: Post) {
        thread {
            editState.postValue(PostEditState(status = PostEditState.Status.SAVING))
            try {
                repository.save(post)
                _postCreated.postValue(Unit)
                PostEditState(status = PostEditState.Status.OK)
            } catch (e: Exception) {
                PostEditState(status = PostEditState.Status.ERROR)
            }.also(editState::postValue)
        }
    }

    fun getNewPost() = Post(
        id = 0,
        content = "",
        author = "Me",
        published = 0
    )
}