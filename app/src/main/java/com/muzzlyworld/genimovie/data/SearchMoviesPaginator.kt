package com.muzzlyworld.genimovie.data

import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.model.MovieShortcuts
import com.muzzlyworld.genimovie.util.model.Paged
import com.muzzlyworld.genimovie.util.model.Result

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