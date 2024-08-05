package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.netology.nmedia.dto.Post

@Entity(primaryKeys = ["id", "localId"])
class PostEntity(
    var id: Long = 0L,
    var localId: Long = 0L,
    val author: String,
    val content: String,
    val videoUrl: String? = "",
    val published: Long,
    var likesCount: Long = 0,
    val sharesCount: Long = 0,
    val viewsCount: Long = 0,
    var likedByMe: Boolean = false,
    var localState: Int = 0,
    var localIsNew: Boolean? = null
) {
    val isOk: Boolean
        get() = localState == STATE_OK
    var isDeleted: Boolean
        get() = (localState and STATE_DELETED) != 0
        set(value: Boolean) {
            localState =
                if (value) localState or STATE_DELETED else localState and STATE_DELETED.inv()
        }

    var isUnsaved: Boolean
        get() = (localState and STATE_UNSAVED) != 0
        set(value: Boolean) {
            localState =
                if (value) localState or STATE_UNSAVED else localState and STATE_UNSAVED.inv()
        }

    var isLikeUpdated: Boolean
        get() = (localState and STATE_LIKE_UPDATED) != 0
        set(value: Boolean) {
            localState =
                if (value) localState or STATE_LIKE_UPDATED else localState and STATE_LIKE_UPDATED.inv()
        }

    val isUpdated: Boolean
        get() = (localState and STATE_ALL_UPDATES) != 0

    companion object {
        val STATE_OK = 0
        val STATE_DELETED = 1
        val STATE_UNSAVED = 2
        val STATE_LIKE_UPDATED = 4
        val STATE_SHARE_UPDATED = 8
        val STATE_ALL_UPDATES = STATE_UNSAVED or STATE_LIKE_UPDATED or STATE_SHARE_UPDATED

        var nextLocalId: Long = 0L
            get() {
                if (field == 0L) field = System.currentTimeMillis()
                else field++
                return field
            }

        fun fromDto(post: Post) = PostEntity(
            id = post.id,
            localId = post.localId,
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

    fun toDto() = Post(
        id = id,
        localId = localId,
        author = author,
        content = content,
        videoUrl = videoUrl,
        published = published,
        likes = likesCount,
        sharesCount = sharesCount,
        viewsCount = viewsCount,
        likedByMe = likedByMe,
    )
}

fun List<PostEntity>.toDto() = map(PostEntity::toDto)
fun List<Post>.toEntity() = map(PostEntity::fromDto)
fun List<Post>.toNewEntity() = map {
    val entity = PostEntity.fromDto(it)
    entity.localIsNew = true
    entity
}
