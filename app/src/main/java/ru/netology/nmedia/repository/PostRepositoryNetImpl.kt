package ru.netology.nmedia.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.RuntimeException

class PostRepositoryNetImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()

        fun getUrl() = BASE_URL
    }

    override fun getAllAsync(callback: PostRepository.GetPostsCallback) {
        val request : Request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(Json.decodeFromString<List<Post>>(response.body?.string() ?: throw RuntimeException("Body is empty")))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun likeByMeAsync(id: Long, callback: PostRepository.GetPostCallback) =
        updatePost(
            Request.Builder()
                .url("$BASE_URL/api/posts/$id/likes")
                .post("".toRequestBody(PostRepositoryNetImpl.jsonType)),
            callback
        )

    override fun unlikeByMeAsync(id: Long, callback: PostRepository.GetPostCallback) =
        updatePost(
            Request.Builder()
                .url("$BASE_URL/api/posts/$id/likes")
                .delete(),
            callback
        )

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemovePostCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onSuccess(id)
                }

            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetPostCallback) =
        updatePost(
            Request.Builder()
                .url("$BASE_URL/api/posts")
                .post(Json.encodeToString(post).toRequestBody(jsonType)),
            callback
        )

    private fun updatePost(requestBuilder: Request.Builder, callback: PostRepository.GetPostCallback) {
        val request = requestBuilder.build()

        client.newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(Json.decodeFromString<Post>(response.body?.string() ?: throw RuntimeException("Body is empty")))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }
}