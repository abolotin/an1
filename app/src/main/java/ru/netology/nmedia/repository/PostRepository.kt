package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: GetPostsCallback);
    fun likeByMeAsync(id: Long, callback: GetPostCallback)
    fun unlikeByMeAsync(id: Long, callback: GetPostCallback)
    fun removeByIdAsync(id: Long, callback: RemovePostCallback)
    fun saveAsync(post: Post, callback: GetPostCallback)

    interface GetPostsCallback {
        fun onSuccess(loadedPosts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface GetPostCallback {
        fun onSuccess(loadedPost: Post) {}
        fun onError(e: Exception) {}
    }

    interface RemovePostCallback {
        fun onSuccess(id: Long) {}
        fun onError(e: Exception) {}
    }
}