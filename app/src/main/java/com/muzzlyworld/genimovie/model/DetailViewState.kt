package com.muzzlyworld.genimovie.model

data class DetailViewState(
    val detailMovie: DetailMovie?,
    val isLoading: Boolean
){

    companion object{
        fun idle() =
            DetailViewState(
                detailMovie = null,
                isLoading = false
            )
    }
}