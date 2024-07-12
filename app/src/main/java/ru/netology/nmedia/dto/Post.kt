package ru.netology.nmedia.dto

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val videoUrl: String? = "",
    val published: Long,
    var likes: Long = 0,
    var sharesCount: Long = 0,
    var viewsCount: Long = 0,
    var likedByMe: Boolean = false,
    var attachment: Attachment? = null
) {
    @Serializable
    data class Attachment(
        var url: String,
        var description: String? = null,
        var type: AttachmentType = AttachmentType.IMAGE
    ) {
        enum class AttachmentType {
            IMAGE
        }
    }
}
