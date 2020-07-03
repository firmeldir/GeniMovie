package com.muzzlyworld.genimovie.network.model

import com.muzzlyworld.genimovie.model.DetailMovie
import com.muzzlyworld.genimovie.model.ParticipantActor
import com.muzzlyworld.genimovie.network.IMAGE_URL_HEADER
import com.squareup.moshi.Json

private const val CAST_MAX_SIZE = 15


data class DetailMovieResponse(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "poster_path") val posterUrl: String?,
    @field:Json(name = "overview") val description: String,
    @field:Json(name = "release_date") val releaseDate: String,
    @field:Json(name = "genres") val genresList: List<GenreResponse>,
    @field:Json(name = "credits") val credits: CreditsResponse
){

    fun toDetailMovie() =
        DetailMovie(
            title = title,
            posterUrl = posterUrl?.let { IMAGE_URL_HEADER + it },
            description = description,
            releaseDate = releaseDate,
            genresList = genresList.map { it.name },
            directorsList = credits.crew.filter { it.job ==  JOB_DIRECTOR}.map { it.name },

            cast = credits.cast.take(CAST_MAX_SIZE).map { it.toParticipantActor() }
        )


    companion object{
        private const val JOB_DIRECTOR = "Director"
    }
}

data class GenreResponse(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String
)

data class CreditsResponse(
    @field:Json(name = "cast") val cast: List<CastResponse>,
    @field:Json(name = "crew") val crew: List<CrewResponse>
)

data class CastResponse(
    @field:Json(name = "character") val character: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "order") val order: Int,
    @field:Json(name = "profile_path") val profileUrl: String?
){

    fun toParticipantActor() =
        ParticipantActor(
            name = name,
            character = character,
            castPosition = order,
            profileUrl = profileUrl?.let { IMAGE_URL_HEADER + it }
        )
}

data class CrewResponse(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "job") val job: String
)

