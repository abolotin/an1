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

    override fun getById(id: Long): Post? {
        return posts.firstOrNull { it.id == id }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = dao.getAll()
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
        posts = dao.getAll()
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
    }

    override fun save(post: Post) {

        val saved = dao.save(post)
        posts = if (post.id == 0L)
            listOf(saved) + posts
        else
            posts.map { if (it.id == post.id) saved else it }
    }
}