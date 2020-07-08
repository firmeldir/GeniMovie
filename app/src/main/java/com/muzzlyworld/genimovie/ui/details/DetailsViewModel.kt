package com.muzzlyworld.genimovie.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzzlyworld.genimovie.model.DetailViewState
import com.muzzlyworld.genimovie.model.Event
import com.muzzlyworld.genimovie.model.Result
import com.muzzlyworld.genimovie.repository.MovieRepository
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val movieRepository: MovieRepository
): ViewModel(){

    private val _detailMovieViewState = MediatorLiveData<DetailViewState>().apply { value = DetailViewState.idle() }
    val detailMovieViewState: LiveData<DetailViewState> get() = _detailMovieViewState

    private val _errorMessage = MediatorLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    fun loadDetailMovie(id: String) = withState {
        if(it.detailMovie != null) return@withState

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            (movieRepository.getDetailMovie(id) as? Result.Success)?.let {
                setState { copy(isLoading = false, detailMovie = it.data) }
            } ?: kotlin.run { setState { copy(isLoading = false) }.apply { sendErrorMessage() } }
        }
    }

    private fun withState(block: (state: DetailViewState) -> Unit){
        block(_detailMovieViewState.value!!)
    }

    private fun setState(reducer: DetailViewState.() -> DetailViewState){
        _detailMovieViewState.value = reducer(_detailMovieViewState.value!!)
    }

    private fun sendErrorMessage(){ _errorMessage.value = Event("Unknown Error. Check Internet connection" ) }
}