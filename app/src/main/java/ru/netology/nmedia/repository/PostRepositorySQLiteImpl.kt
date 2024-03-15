package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            data.value = field
        }
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
    }

    /*private fun sync() {
        context.openFileOutput(postsFile, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts, gsonType))
        }
    }*/

    override fun getById(id: Long): Post? {
        return posts.firstOrNull { it.id == id }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = dao.getAll()
    }

    override fun shareById(id: Long) {
        /*posts = posts.map {
            if (it.id == id) {
                it.copy(sharesCount = it.sharesCount + 1)
            } else it
        }*/
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
    }

    override fun save(post: Post) {

        val saved = dao.save(post)
        posts = if (post.id == 0L)
            listOf(saved) + posts
        else
            posts.map { if (it.id == post.id) saved else it }

        /*
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
        */
    }
}