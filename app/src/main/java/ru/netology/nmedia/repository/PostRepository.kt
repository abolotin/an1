package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PhotoModel

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun loadAll()
    suspend fun getLocalOne(id: Long, localId: Long): Post?
    suspend fun getLocalLast(): Post?
    suspend fun likeByMe(id: Long)
    suspend fun unlikeByMe(id: Long)
    suspend fun remove(id: Long, localId: Long)
    suspend fun save(post: Post, photo: PhotoModel? = null)
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun setAllViewed()
}