package com.muzzlyworld.genimovie.model

data class DetailMovie(
    val title: String,
    val posterUrl: String?,
    val description: String,
    val releaseDate: String,
    val genresList: List<String>,
    val directorsList: List<String>,

    val cast: List<ParticipantActor>
)

data class ParticipantActor(
    val name: String,
    val character: String,
    val castPosition: Int,
    val profileUrl: String?
)
