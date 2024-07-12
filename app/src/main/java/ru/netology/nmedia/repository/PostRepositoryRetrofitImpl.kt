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
    override fun getAllAsync(callback: PostRepository.GetPostsCallback) {
        PostsApiImpl.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if(response.isSuccessful) {
                        callback.onSuccess(response.body() ?: throw RuntimeException("Body is empty"))
                    } else {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(RuntimeException(t.message))
                }

            })
    }

    override fun likeByMeAsync(id: Long, callback: PostRepository.GetPostCallback) =
        PostsApiImpl.retrofitService.likeByMe(id)
            .enqueue(singlePostCallback(id, callback))
    override fun unlikeByMeAsync(id: Long, callback: PostRepository.GetPostCallback) =
        PostsApiImpl.retrofitService.unlikeByMe(id)
            .enqueue(singlePostCallback(id, callback))

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemovePostCallback) {
        PostsApiImpl.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if(response.isSuccessful) {
                        callback.onSuccess(id)
                    } else {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t.message))
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetPostCallback) =
        PostsApiImpl.retrofitService.save(post)
            .enqueue(singlePostCallback(post.id, callback))

    private fun singlePostCallback(postId: Long, callback: PostRepository.GetPostCallback) = object : Callback<Post> {
        override fun onResponse(call: Call<Post>, response: Response<Post>) {
            if(response.isSuccessful) {
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is empty"))
            } else {
                callback.onError(RuntimeException(response.message()), postId)
            }
        }

        override fun onFailure(call: Call<Post>, t: Throwable) {
            callback.onError(RuntimeException(t.message), postId)
        }

    }
}