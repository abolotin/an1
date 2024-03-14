package ru.netology.nmedia.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInFileImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryInFileImpl(application)
    val data = repository.getAll()

    fun getById(id: Long) = repository.getById(id)
    fun likeById(id: Long) = repository.likeById(id)

    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long) = repository.removeById(id)

    fun savePost(post: Post) = repository.save(post)

    fun getNewPost() = Post(
        id = 0,
        content = "",
        author = "",
        published = ""
    )
}