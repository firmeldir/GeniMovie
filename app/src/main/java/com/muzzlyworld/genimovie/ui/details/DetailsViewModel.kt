package com.muzzlyworld.genimovie.ui.details

import androidx.lifecycle.viewModelScope
import com.muzzlyworld.genimovie.data.MovieRepository
import com.muzzlyworld.genimovie.model.DetailsViewState
import com.muzzlyworld.genimovie.ui.StatefulViewModel
import com.muzzlyworld.genimovie.util.model.Result
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val movieRepository: MovieRepository
): StatefulViewModel<DetailsViewState>(){

    override fun idleViewState(): DetailsViewState = DetailsViewState.idle()

    fun loadDetailMovie(id: String) = withState {
        if(it.detailMovie != null) return@withState

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            (movieRepository.getDetailMovie(id) as? Result.Success)?.let {
                setState { copy(isLoading = false, detailMovie = it.data) }
            } ?: kotlin.run { setState { copy(isLoading = false) }.apply { sendErrorMessage() } }
        }
    }
}