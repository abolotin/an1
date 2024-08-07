package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll();
    suspend fun likeByMe(id: Long)
    suspend fun unlikeByMe(id: Long)
    suspend fun remove(id: Long, localId: Long)
    suspend fun save(post: Post)
    fun getNewerCount(id: Long) : Flow<Int>
    suspend fun setAllViewed()
}