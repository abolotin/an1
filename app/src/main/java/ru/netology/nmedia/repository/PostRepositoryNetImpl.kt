package ru.netology.nmedia.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit
import kotlin.RuntimeException

class PostRepositoryNetImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }
    override fun getById(id: Long): Post? {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Post> {
        val request : Request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null")
            }
            .let {
                Json.decodeFromString<List<Post>>(it)
            }
    }

    override fun likeByMe(id: Long): Post {
        val request : Request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .post("".toRequestBody(jsonType))
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null")
            }
            .let {
                Json.decodeFromString<Post>(it)
            }
    }

    override fun unlikeByMe(id: Long): Post {
        val request : Request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .delete()
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null")
            }
            .let {
                Json.decodeFromString<Post>(it)
            }
    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request : Request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .post(Json.encodeToString(post).toRequestBody(jsonType))
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string() ?: throw RuntimeException("body is null")
            }
            .let {
                Json.decodeFromString<Post>(it)
            }
    }
}