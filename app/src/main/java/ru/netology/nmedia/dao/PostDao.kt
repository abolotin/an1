package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    //@Query("SELECT * FROM PostEntity ORDER BY localId DESC, id DESC")
    //fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity ORDER BY localId DESC, id DESC")
    fun getAllPaged(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long): PostEntity?

    @Query("SELECT * FROM PostEntity WHERE id = :id AND localId = :localId")
    suspend fun getByLocalId(id: Long, localId: Long): PostEntity?

    @Query("SELECT * FROM PostEntity ORDER BY id DESC LIMIT 1")
    suspend fun getLast(): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id AND localId = :localId")
    suspend fun removePost(id: Long, localId: Long)

    @Query("UPDATE PostEntity SET localIsNew = false")
    suspend fun setAllViewed()
}