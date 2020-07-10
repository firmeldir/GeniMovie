package com.muzzlyworld.genimovie.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.muzzlyworld.genimovie.data.MovieRepository
import com.muzzlyworld.genimovie.data.SearchMoviesPaginator
import com.muzzlyworld.genimovie.data.TrendingMoviesPaginator
import com.muzzlyworld.genimovie.model.MovieShortcuts
import com.muzzlyworld.genimovie.model.SearchViewState
import com.muzzlyworld.genimovie.ui.StatefulViewModel
import com.muzzlyworld.genimovie.util.model.Event
import com.muzzlyworld.genimovie.util.model.Result
import kotlinx.coroutines.launch

class ListViewModel(
    movieRepository: MovieRepository,

    private val trendingMoviesPaginator: TrendingMoviesPaginator = TrendingMoviesPaginator(movieRepository),
    private val searchMoviesPaginator: SearchMoviesPaginator = SearchMoviesPaginator(movieRepository)
): StatefulViewModel<SearchViewState>(){

    init { loadTrendingMovies() }

    private val _keyboardShowAction = MediatorLiveData<Event<Boolean>>()
    val keyboardShowAction: LiveData<Event<Boolean>> get() = _keyboardShowAction

    override fun idleViewState(): SearchViewState = SearchViewState.idle()


    fun onSearchClick() = withState {
        if(it.isSearching){
            _keyboardShowAction.value = Event(false)

            setState { copy(searchingQuery = null) }
            loadTrendingMovies()
        }
        else _keyboardShowAction.value = Event(true)
    }

    private fun loadTrendingMovies() = viewModelScope.launch {
        setState { copy(isLoading = true) }

        (trendingMoviesPaginator.loadInitialData() as? Result.Success)?.let { success ->
            setState { copy(isLoading = false, searchMovies = (success.data)) }
        } ?: kotlin.run { setState { copy(isLoading = false) }.apply { sendErrorMessage() } }
    }

    fun searchForQuery(query: String) = withState {
        if(query == it.searchingQuery) return@withState

        viewModelScope.launch {
            setState { copy(isLoading = true, searchingQuery = query) }

            (searchMoviesPaginator.loadInitialWithQuery(query) as? Result.Success)?.let {
                setState { copy(isLoading = false, searchMovies = (it.data)) }
            } ?: kotlin.run { setState { copy(isLoading = false) }.apply { sendErrorMessage() } }
        }
    }

    fun loadNextMovies(lastVisibleItemPosition: Int) = withState {
        if(it.isSearching) loadNextSearchedMovies(lastVisibleItemPosition)
        else loadNextTrendingMovies(lastVisibleItemPosition)
    }

    private fun loadNextTrendingMovies(lastVisibleItemPosition: Int) = withState {
        if(!checkIfTrendingMoviesCanBeLoaded(it, lastVisibleItemPosition)) return@withState
        viewModelScope.launch { addNextMovieShortcuts(trendingMoviesPaginator.loadNextData()) }
    }

    private fun loadNextSearchedMovies(lastVisibleItemPosition: Int) = withState {
        if(!checkIfSearchedMoviesCanBeLoaded(it, lastVisibleItemPosition)) return@withState
        viewModelScope.launch { addNextMovieShortcuts(searchMoviesPaginator.loadNextData()) }
    }

    private fun addNextMovieShortcuts(result: Result<MovieShortcuts>) = withState { state ->
        (result as? Result.Success)?.let { success ->
            setState { copy(searchMovies = state.searchMovies + success.data) }
        } ?: kotlin.run { sendErrorMessage() }
    }


    private fun checkIfTrendingMoviesCanBeLoaded(
        state: SearchViewState,
        lastVisibleItemPosition: Int
    ) = (lastVisibleItemPosition >= state.searchMovies.size - 2) && !(trendingMoviesPaginator.isAllDataLoaded())

    private fun checkIfSearchedMoviesCanBeLoaded(
        state: SearchViewState,
        lastVisibleItemPosition: Int
    ) = (lastVisibleItemPosition >= state.searchMovies.size - 2) && !(searchMoviesPaginator.isAllDataLoaded())
}