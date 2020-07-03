package com.muzzlyworld.genimovie.ui.list.util

import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.model.Paged
import com.muzzlyworld.genimovie.model.Result
import com.muzzlyworld.genimovie.repository.MovieRepository
import com.muzzlyworld.genimovie.util.PagePaginator

class TrendingMoviesPaginator(
    private val movieRepository: MovieRepository
) : PagePaginator<MovieShortcut>(){

    override suspend fun loadInitial(): Result<Paged<MovieShortcut>> = movieRepository.getTrendingMovies()

    override suspend fun loadNext(page: Int): Result<Paged<MovieShortcut>> = movieRepository.getTrendingMovies(page)
}