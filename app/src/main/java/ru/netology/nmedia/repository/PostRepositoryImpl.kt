package ru.netology.nmedia.repository

import android.database.SQLException
import androidx.lifecycle.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.api.PostsApiImpl
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PhotoModel
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.entity.toNewEntity
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.AppErrors
import ru.netology.nmedia.errors.DatabaseError
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.errors.UnknownError
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data = postDao.getAll()
        .flowOn(Dispatchers.Default)
        .map { entityList ->
            val updatedList = entityList.filter { it.isUpdated }
            val removedList = entityList.filter { it.isDeleted }
            saveOnServer(updatedList)
            removeOnServer(removedList)
            entityList.filter {
                !it.isDeleted && !(it.localIsNew ?: false)
            }
                .toDto()
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

    override suspend fun save(post: Post, photo: PhotoModel?) {
        var postEntity: PostEntity?
        var localPost = post.copy()

        try {
            photo?.file?.let {
                // Загружаем фото на сервер
                val response = PostsApiImpl.retrofitService.upload(
                    MultipartBody.Part.createFormData(
                        "file",
                        it.name,
                        it.asRequestBody()
                    )
                )
                if (!response.isSuccessful) throw ApiError(response.code(), response.message())
                val mediaResponse = response.body() ?: throw ApiError(response.code(), response.message())
                localPost.attachment = Post.Attachment(url = mediaResponse.id)
            }

            // Сохраняем пост в локальной БД
            postEntity = PostEntity.fromDto(localPost)
            postEntity.isUnsaved = true
            postDao.insert(postEntity)
            if (localPost.id !== 0L) setAllViewed()
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

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            try {
                val response = PostsApiImpl.retrofitService.getNewer(id)

                if (!response.isSuccessful)
                    throw ApiError(response.code(), response.message())

                val body = response.body() ?: throw ApiError(response.code(), response.message())
                val entities = body.toNewEntity()
                postDao.insert(entities)
                emit(entities.size)
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiError) {
                throw e
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        }
    }
        .catch { e -> throw AppErrors.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun setAllViewed() {
        try {
            postDao.setAllViewed()
        } catch (e: SQLException) {
            throw DatabaseError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}