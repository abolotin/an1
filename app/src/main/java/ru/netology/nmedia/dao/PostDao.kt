package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getById(id: Long): PostEntity?

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content = :content, videoUrl = :videoUrl WHERE id = :id")
    fun updateById(id: Long, content: String, videoUrl: String)

    fun save(post: PostEntity) = if (post.id == 0L) insert(post) else updateById(post.id, post.content, post.videoUrl)

    @Query("""
        UPDATE PostEntity SET
                    likesCount = likesCount + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                    likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = :id
    """)
    fun likeByMe(id: Long)

    @Query("""
        UPDATE PostEntity SET
                    likesCount = likesCount + 1
                WHERE id = :id
    """)
    fun likeById(id: Long)

    @Query("""
                        UPDATE PostEntity SET
                    sharesCount = sharesCount + 1
                WHERE id = :id
    """)
    fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)
}