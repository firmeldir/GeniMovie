package com.muzzlyworld.genimovie.util

import com.muzzlyworld.genimovie.model.Paged
import com.muzzlyworld.genimovie.model.Result
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext

abstract class PagePaginator<V : Any> {

    private var loadInitialDataJob: Job? = null
    private var loadNextDataJob: Job? = null

    private var page: Int? = null

    suspend fun loadInitialData(): Result<List<V>> {
        loadNextDataJob?.cancel()
        loadInitialDataJob = coroutineContext[Job]

        return loadInitial().onSuccess { setPaginationPage(it) }?.
        let { Result.Success(it.data.data) } ?: Result.Error(Exception(""))
    }

    suspend fun loadNextData(): Result<List<V>> {
        kotlin.runCatching { loadInitialDataJob?.join() }
        requireNotNull(page) { "All data loaded" }

        loadNextDataJob = coroutineContext[Job]

        return loadNext(page!!).onSuccess { setPaginationPage(it) }
            ?.let { Result.Success(it.data.data) } ?: Result.Error(Exception(""))
    }

    fun isAllDataLoaded() = page == null

    protected abstract suspend fun loadInitial() : Result<Paged<V>>
    protected abstract suspend fun loadNext(page: Int) : Result<Paged<V>>

    private fun setPaginationPage(data: Paged<V>) {
        page = data.takeIf { data.page != data.total }?.let { data.page + 1 }
    }
}