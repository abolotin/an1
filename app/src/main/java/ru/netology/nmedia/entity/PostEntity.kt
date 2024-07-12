package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val videoUrl: String? = "",
    val published: Long,
    val likesCount: Long = 0,
    val sharesCount: Long = 0,
    val viewsCount: Long = 0,
    val likedByMe: Boolean = false
) {
    fun toDto() = Post(
        id = id,
        author = author,
        content = content,
        videoUrl = videoUrl,
        published = published,
        likes = likesCount,
        sharesCount = sharesCount,
        viewsCount = viewsCount,
        likedByMe = likedByMe
    )

    companion object {
        fun fromDto(post: Post) = PostEntity(
            id = post.id,
            author = post.author,
            content = post.content,
            videoUrl = post.videoUrl,
            published = post.published,
            likesCount = post.likes,
            sharesCount = post.sharesCount,
            viewsCount = post.viewsCount,
            likedByMe = post.likedByMe
        )
    }
}