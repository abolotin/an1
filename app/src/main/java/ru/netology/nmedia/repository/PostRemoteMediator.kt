package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.App
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.errors.ApiError

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val dao: PostDao,
    private val api: Api,
    private val keyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.APPEND -> {
                    val id = keyDao.min() ?: return MediatorResult.Success(false)
                    api.getBefore(id, state.config.pageSize)
                }

                LoadType.REFRESH -> {
                    val id = keyDao.max()
                    if (id == null) api.getLatest(state.config.pageSize)
                    else api.getAfter(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                    //val id = keyDao.max() ?: return MediatorResult.Success(false)
                    //api.getAfter(id, state.config.pageSize)
                }
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        // dao.clear()
                        val list = mutableListOf(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                key = body.first().id
                            )
                        )
                        keyDao.max()?.let {
                            list.add(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    key = body.last().id
                                )
                            )
                        }
                        keyDao.insert(list)
                    }

                    LoadType.PREPEND -> {
                        keyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    key = body.first().id
                                )
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        keyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    key = body.last().id
                                )
                            )
                        )
                    }
                }
                dao.insert(body.map(PostEntity::fromDto))
            }

            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }

    }
}