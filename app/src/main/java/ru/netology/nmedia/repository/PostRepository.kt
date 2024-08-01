package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll();
    suspend fun likeByMe(id: Long)
    suspend fun unlikeByMe(id: Long)
    suspend fun remove(id: Long, localId: Long)
    suspend fun save(post: Post)
}