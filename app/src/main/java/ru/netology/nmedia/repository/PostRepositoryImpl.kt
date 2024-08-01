package ru.netology.nmedia.repository

import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.PostsApiImpl
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.errors.UnknownError
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data = postDao.getAll().map { entityList ->
        val updatedList = entityList.filter { it.isUpdated }
        val removedList = entityList.filter { it.isDeleted }
        saveOnServer(updatedList)
        removeOnServer(removedList)
        entityList.filter {
            !it.isDeleted
        }.toDto()
    }

    override suspend fun getAll() {
        try {
            val response = PostsApiImpl.retrofitService.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            response.body()?.let {
                postDao.insert(it.toEntity())
            }
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeByMe(id: Long) {
        try {
            val existingPost = postDao.getById(id)
            existingPost?.let {
                existingPost.likedByMe = true
                existingPost.likesCount++
                existingPost.isLikeUpdated = true
                postDao.insert(existingPost)
            }
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unlikeByMe(id: Long) {
        try {
            val existingPost = postDao.getById(id)
            existingPost?.let {
                existingPost.likedByMe = false
                existingPost.likesCount--
                existingPost.isLikeUpdated = true
                postDao.insert(existingPost)
            }
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun remove(id: Long, localId: Long) {
        var existingPost: PostEntity? = null

        try {
            if (localId != 0L) {
                // Запись не была сохранена на сервере. Просто удаляем её из БД
                postDao.removePost(id, localId)
            } else {
                existingPost = postDao.getById(id)
                existingPost?.let {
                    existingPost.isDeleted = true
                    postDao.insert(existingPost)
                }
            }
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        var postEntity: PostEntity? = null

        // Сохраняем пост в локальной БД
        try {
            postEntity = PostEntity.fromDto(post)
            postEntity.isUnsaved = true
            postDao.insert(postEntity)
        } catch (e: Exception) {
            // Ошибка конвертации или сохранения в БД
            throw UnknownError
        }
    }

    private fun removeOnServer(removedPosts: List<PostEntity>) {
        CoroutineScope(EmptyCoroutineContext).launch {
            val listIterator = removedPosts.iterator()
            try {
                while (listIterator.hasNext()) {
                    removeOnServer(listIterator.next())
                }
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun removeOnServer(post: PostEntity) {
        try {
            val response = PostsApiImpl.retrofitService.removeById(post.id)
            if (!response.isSuccessful) throw Exception()
            postDao.removePost(post.id, post.localId)
        } catch (e: Exception) {
        }
    }

    private fun saveOnServer(unsavedPosts: List<PostEntity>) {
        CoroutineScope(EmptyCoroutineContext).launch {
            val listIterator = unsavedPosts.iterator()
            try {
                while (listIterator.hasNext()) {
                    val post = listIterator.next()
                    // Сохраняем пост на сервере
                    if (post.isUnsaved) saveOnServer(post)
                    // Обновляем лайк на сервере
                    if (post.isLikeUpdated) likeOnServer(post)
                }
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun saveOnServer(post: PostEntity) {
        try {
            val response = PostsApiImpl.retrofitService.save(post.toDto())
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            response.body()?.let {
                // Заменяем локальную запись на серверную.
                // Альтернатива: использование запроса UPDATE с условием по полям id и localId
                postDao.removePost(post.id, post.localId)
                postDao.insert(PostEntity.fromDto(it))
            }
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun likeOnServer(post: PostEntity) {
        if (post.id == 0L)
            // Нельзя лайкнуть несохранённый пост
            return

        try {
            val response = if (post.likedByMe)
                PostsApiImpl.retrofitService.likeByMe(post.id)
            else
                PostsApiImpl.retrofitService.unlikeByMe(post.id)

            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            response.body()?.let {
                // Заменяем локальную запись на серверную.
                // Альтернатива: использование запроса UPDATE с условием по полям id и localId
                postDao.removePost(post.id, post.localId)
                postDao.insert(PostEntity.fromDto(it))
            }
        } catch (e: ApiError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}