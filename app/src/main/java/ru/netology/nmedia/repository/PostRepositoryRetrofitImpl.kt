/*
package ru.netology.nmedia.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.PostsApiImpl
import ru.netology.nmedia.dto.Post
import java.io.IOException
import kotlin.RuntimeException

class PostRepositoryRetrofitImpl : PostRepository {
    override suspend fun getAll() = PostsApiImpl.retrofitService.getAll()

    override suspend fun likeByMe(id: Long) = PostsApiImpl.retrofitService.likeByMe(id)

    override suspend fun unlikeByMe(id: Long) = PostsApiImpl.retrofitService.unlikeByMe(id)

    override suspend fun removeById(id: Long) = PostsApiImpl.retrofitService.removeById(id)

    override suspend fun save(post: Post) = PostsApiImpl.retrofitService.save(post)
}
*/