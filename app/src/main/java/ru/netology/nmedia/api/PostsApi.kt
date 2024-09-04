package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG)
            level = HttpLoggingInterceptor.Level.BODY
    })
    .addInterceptor {chain ->
        chain.proceed(
            chain.run {
                AppAuth.getInstance().state.value?.token?.let {
                    return@run request()
                        .newBuilder()
                        .addHeader("Authorization", it)
                        .build()
                }
                request()
            }
        )
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_URL + "api/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PostsApi {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @POST("posts/{id}/likes")
    suspend fun likeByMe(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikeByMe(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long) : Response<List<Post>>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @Multipart
    @POST("users/registration")
    suspend fun signIn(@Part("login") login: RequestBody, @Part("pass") pass: RequestBody, @Part("name") name: RequestBody, @Part media: MultipartBody.Part): Response<Token>

    @Multipart
    @POST("users/authentication")
    suspend fun signUp(@Part login: MultipartBody.Part, @Part pass: MultipartBody.Part): Response<Token>
}

object PostsApiImpl {
    val retrofitService: PostsApi by lazy {
        retrofit.create(PostsApi::class.java)
    }
}