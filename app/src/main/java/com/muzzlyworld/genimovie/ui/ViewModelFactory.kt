package com.muzzlyworld.genimovie.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muzzlyworld.genimovie.repository.MovieRepository
import com.muzzlyworld.genimovie.ui.details.DetailsViewModel
import com.muzzlyworld.genimovie.ui.list.ListViewModel

class ViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(DetailsViewModel::class.java) -> DetailsViewModel(repository) as T

            modelClass.isAssignableFrom(ListViewModel::class.java) -> ListViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
}
