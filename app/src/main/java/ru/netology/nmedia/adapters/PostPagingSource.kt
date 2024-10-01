package ru.netology.nmedia.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.errors.ApiError

class PostPagingSource(
    private val service: Api,
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>) = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Append -> service.getBefore(params.key, params.loadSize)
                is LoadParams.Refresh -> service.getLatest(params.loadSize)
                is LoadParams.Prepend -> {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = params.key,
                        nextKey = null
                    )
                }
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message()
            )

            val nextKey = if (body.isEmpty()) null else body.last().id
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}