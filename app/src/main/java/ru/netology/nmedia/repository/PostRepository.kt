package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getById(id: Long): Post?
    fun getAll(): List<Post>
    fun likeByMe(id: Long)
    fun unlikeByMe(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)

    fun save(post: Post)
}