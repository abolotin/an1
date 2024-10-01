package ru.netology.nmedia.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toNewEntity
import ru.netology.nmedia.errors.ApiError
import ru.netology.nmedia.errors.NetworkError
import ru.netology.nmedia.repository.PostRepository

class PostDBPagingSource(
    private val dao: PostDao,
    private val repository: PostRepository,
    private val api: Api
) : PagingSource<Int, PostEntity>() {
    override fun getRefreshKey(state: PagingState<Int, PostEntity>) = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostEntity> = dao.getAllPaged().load(params)
}