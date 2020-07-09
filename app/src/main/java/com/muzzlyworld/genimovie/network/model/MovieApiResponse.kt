package com.muzzlyworld.genimovie.network.model

import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.model.MovieShortcuts
import com.muzzlyworld.genimovie.util.model.Paged
import com.muzzlyworld.genimovie.network.IMAGE_URL_HEADER
import com.squareup.moshi.Json

data class MovieApiResponse(
    @field:Json(name = "page") val page: Int,
    @field:Json(name = "results") val results: List<MovieResponse>,
    @field:Json(name = "total_pages") val total: Int
){
    private fun toMovieShortcuts() : MovieShortcuts =
        this.results.map {
            it.toMovieShortcut()
        }

    fun toPagedMovieShortcuts() =
        Paged(
            toMovieShortcuts(),
            page,
            total
        )
}

data class MovieResponse(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "poster_path") val posterUrl: String?,
    @field:Json(name = "overview") val description: String
){
    fun toMovieShortcut() : MovieShortcut =
        MovieShortcut(
            id,
            title,
            posterUrl?.let { IMAGE_URL_HEADER + it },
            description
        )
}