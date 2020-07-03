package com.muzzlyworld.genimovie.model

data class SearchViewState(
    val searchMovies: MovieShortcuts,
    val isLoading: Boolean,
    val searchingQuery: String?     //Null if not searching(popular)
){

    val isSearching: Boolean get() = searchingQuery != null

    companion object{
        fun idle() =
            SearchViewState(
                searchingQuery = null,
                isLoading = false,
                searchMovies = emptyList()
            )
    }
}