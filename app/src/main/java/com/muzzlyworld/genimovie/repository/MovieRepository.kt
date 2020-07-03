package com.muzzlyworld.genimovie.repository

import com.muzzlyworld.genimovie.model.DetailMovie
import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.model.Paged
import com.muzzlyworld.genimovie.model.Result

interface MovieRepository {

    suspend fun getTrendingMovies(page: Int = 1): Result<Paged<MovieShortcut>>

    suspend fun searchForMovies(query: String, page: Int = 1): Result<Paged<MovieShortcut>>

    suspend fun getDetailMovie(id: String): Result<DetailMovie>
}