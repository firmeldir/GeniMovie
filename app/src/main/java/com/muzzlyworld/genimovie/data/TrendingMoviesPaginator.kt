package com.muzzlyworld.genimovie.data

import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.util.model.Paged
import com.muzzlyworld.genimovie.util.model.Result

class TrendingMoviesPaginator(
    private val movieRepository: MovieRepository
) : PagePaginator<MovieShortcut>(){

    override suspend fun loadInitial(): Result<Paged<MovieShortcut>> = movieRepository.getTrendingMovies()

    override suspend fun loadNext(page: Int): Result<Paged<MovieShortcut>> = movieRepository.getTrendingMovies(page)
}