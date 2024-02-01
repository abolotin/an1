package ru.netology.nmedia.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val emptyPost = Post(
    id = 0,
    content = "",
    author = "",
    published = ""
)
class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val editedPost = MutableLiveData(emptyPost)

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun cancelEdit() {
        editedPost.value = emptyPost
    }

    fun save() {
        editedPost.value?.let{
            repository.save(it)
        }
        editedPost.value = emptyPost
    }

    fun updateContent(content: String) {
        editedPost.value?.let{
            val text = content.trim()
            if (it.content == text) return
            editedPost.value = it.copy(content = content)
        }
    }
}