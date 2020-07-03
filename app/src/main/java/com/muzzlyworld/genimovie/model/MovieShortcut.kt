package com.muzzlyworld.genimovie.model

typealias MovieShortcuts = List<MovieShortcut>

data class MovieShortcut(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val description: String
)