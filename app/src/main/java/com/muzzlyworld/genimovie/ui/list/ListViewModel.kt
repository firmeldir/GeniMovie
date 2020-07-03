package com.muzzlyworld.genimovie.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzzlyworld.genimovie.model.Event
import com.muzzlyworld.genimovie.model.MovieShortcuts
import com.muzzlyworld.genimovie.model.Result
import com.muzzlyworld.genimovie.model.SearchViewState
import com.muzzlyworld.genimovie.network.MovieApiFactory
import com.muzzlyworld.genimovie.repository.DefaultMovieRepository
import com.muzzlyworld.genimovie.repository.MovieRepository
import com.muzzlyworld.genimovie.ui.list.util.SearchMoviesPaginator
import com.muzzlyworld.genimovie.ui.list.util.TrendingMoviesPaginator
import kotlinx.coroutines.launch

class ListViewModel(
    movieRepository: MovieRepository = DefaultMovieRepository(MovieApiFactory.movieApi),
    private val trendingMoviesPaginator: TrendingMoviesPaginator = TrendingMoviesPaginator(movieRepository),
    private val searchMoviesPaginator: SearchMoviesPaginator = SearchMoviesPaginator(movieRepository)
): ViewModel(){

    private val _searchViewState = MediatorLiveData<SearchViewState>().apply { value = SearchViewState.idle() }
    val searchViewState: LiveData<SearchViewState> get() = _searchViewState

    private val _keyboardShowAction = MediatorLiveData<Event<Boolean>>()
    val keyboardShowAction: LiveData<Event<Boolean>> get() = _keyboardShowAction

    private val _errorMessage = MediatorLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    fun onSearchClick() = withState {
        if(it.isSearching){
            _keyboardShowAction.value = Event(false)
            setState { copy(searchingQuery = null) }
            loadTrendingMovies()
        }
        else _keyboardShowAction.value = Event(true)
    }

    init {
        loadTrendingMovies()
    }

    private fun loadTrendingMovies() = viewModelScope.launch {
        setState { copy(isLoading = true) }

        (trendingMoviesPaginator.loadInitialData() as? Result.Success)?.let {
            setState { copy(isLoading = false, searchMovies = (it.data)) }
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
        (result as? Result.Success)?.let {
            setState { copy(searchMovies = state.searchMovies + it.data) }
        } ?: kotlin.run { sendErrorMessage() }
    }

    private fun withState(block: (state: SearchViewState) -> Unit){
        block(_searchViewState.value!!)
    }

    private fun setState(reducer: SearchViewState.() -> SearchViewState){
        _searchViewState.value = reducer(_searchViewState.value!!)
    }

    private fun checkIfTrendingMoviesCanBeLoaded(
        state: SearchViewState,
        lastVisibleItemPosition: Int
    ) = (lastVisibleItemPosition >= state.searchMovies.size - 2) && !(trendingMoviesPaginator.isAllDataLoaded())

    private fun checkIfSearchedMoviesCanBeLoaded(
        state: SearchViewState,
        lastVisibleItemPosition: Int
    ) = (lastVisibleItemPosition >= state.searchMovies.size - 2) && !(searchMoviesPaginator.isAllDataLoaded())

    private fun sendErrorMessage(){ _errorMessage.value = Event("Unknown Error. Check Internet connection" ) }
}