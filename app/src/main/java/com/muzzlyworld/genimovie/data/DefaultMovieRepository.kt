package com.muzzlyworld.genimovie.data

import com.muzzlyworld.genimovie.model.DetailMovie
import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.network.MovieApi
import com.muzzlyworld.genimovie.network.model.DetailMovieResponse
import com.muzzlyworld.genimovie.network.model.MovieApiResponse
import com.muzzlyworld.genimovie.util.model.Paged
import com.muzzlyworld.genimovie.util.model.Result
import com.muzzlyworld.genimovie.util.saveApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultMovieRepository(
    private val api: MovieApi
) : MovieRepository {

    override suspend fun getTrendingMovies(page: Int): Result<Paged<MovieShortcut>> = withContext(Dispatchers.IO) {
        saveApiCall(
            call = { api.getTrending(page) },
            transform = MovieApiResponse::toPagedMovieShortcuts
        )
    }


    override suspend fun searchForMovies(query: String, page: Int): Result<Paged<MovieShortcut>> = withContext(Dispatchers.IO) {
        saveApiCall(
            call = { api.searchByQuery(query, page) },
            transform = MovieApiResponse::toPagedMovieShortcuts
        )
    }


    override suspend fun getDetailMovie(id: String): Result<DetailMovie> = withContext(Dispatchers.IO) {
        saveApiCall(
            call = { api.getDetailMovieById(id) },
            transform = DetailMovieResponse::toDetailMovie
        )
    }
}