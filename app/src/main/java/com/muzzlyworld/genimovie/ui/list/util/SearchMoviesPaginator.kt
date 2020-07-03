package com.muzzlyworld.genimovie.ui.list.util

import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.model.MovieShortcuts
import com.muzzlyworld.genimovie.model.Paged
import com.muzzlyworld.genimovie.model.Result
import com.muzzlyworld.genimovie.repository.MovieRepository
import com.muzzlyworld.genimovie.util.PagePaginator

class SearchMoviesPaginator(
    private val movieRepository: MovieRepository
) : PagePaginator<MovieShortcut>(){

    private var currentQuery: String = ""

    suspend fun loadInitialWithQuery(newQuery: String) : Result<MovieShortcuts> {
        currentQuery = newQuery
        return loadInitialData()
    }

    override suspend fun loadInitial(): Result<Paged<MovieShortcut>>
        = movieRepository.searchForMovies(currentQuery)

    override suspend fun loadNext(page: Int): Result<Paged<MovieShortcut>>
            = movieRepository.searchForMovies(currentQuery, page)
}