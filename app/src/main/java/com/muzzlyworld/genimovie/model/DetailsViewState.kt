package com.muzzlyworld.genimovie.model

data class DetailsViewState(
    val detailMovie: DetailMovie?,
    val isLoading: Boolean
) : ViewState{

    companion object{
        fun idle() =
            DetailsViewState(
                detailMovie = null,
                isLoading = false
            )
    }
}