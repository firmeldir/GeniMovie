package com.muzzlyworld.genimovie

import androidx.lifecycle.ViewModelProvider
import com.muzzlyworld.genimovie.network.MovieApiFactory
import com.muzzlyworld.genimovie.data.DefaultMovieRepository
import com.muzzlyworld.genimovie.data.MovieRepository
import com.muzzlyworld.genimovie.ui.ViewModelFactory

/**
 * Class that handles object creation.
 */
object Injection {

    private fun provideMovieRepository(): MovieRepository =
        DefaultMovieRepository(MovieApiFactory.movieApi)

    fun provideViewModelFactory(): ViewModelProvider.Factory =
        ViewModelFactory(provideMovieRepository())
}
