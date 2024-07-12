package ru.netology.nmedia.api

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG)
            level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL + "api/slow/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PostsApi {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts/{id}/likes")
    fun likeByMe(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun unlikeByMe(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long): Call<Unit>

    @POST("posts")
    fun save(@Body post: Post): Call<Post>
}

object PostsApiImpl {
    val retrofitService: PostsApi by lazy {
        retrofit.create(PostsApi::class.java)
    }
}