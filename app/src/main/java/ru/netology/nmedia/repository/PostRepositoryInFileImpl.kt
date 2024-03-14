package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryInFileImpl(
    private val context: Context
) : PostRepository {
    private val gson = Gson()
    private val gsonType = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private var nextId = 0L
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            data.value = field
            sync()
        }
    private val postsFile = "posts.json"
    private val data = MutableLiveData(posts)

    init {
        if (context.filesDir.resolve(postsFile).exists()) {
            context.openFileInput(postsFile).bufferedReader().use {
                posts = gson.fromJson(it, gsonType)
                nextId = posts.maxOfOrNull { it.id }?.inc() ?: 1
            }
        }
    }

    private fun sync() {
        context.openFileOutput(postsFile, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts, gsonType))
        }
    }

    override fun getById(id: Long): Post? {
        return posts.firstOrNull { it.id == id }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id == id)
                it.copy(
                    likesCount = if (it.likedByMe) it.likesCount - 1 else it.likesCount + 1,
                    likedByMe = !it.likedByMe
                )
            else it
        }
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                it.copy(sharesCount = it.sharesCount + 1)
            } else it
        }
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            // Новый пост
            posts = listOf(
                post.copy(
                    id = ++nextId,
                    author = "Me",
                    content = post.content,
                    videoUrl = post.videoUrl
                )
            ) + posts
        } else {
            // Измененный пост
            posts = posts.map {
                if (it.id == post.id) it.copy(
                    content = post.content,
                    videoUrl = post.videoUrl
                ) else it
            }
        }
    }
}