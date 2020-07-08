package com.muzzlyworld.genimovie.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.muzzlyworld.genimovie.R
import com.muzzlyworld.genimovie.databinding.FragmentDetailsBinding
import com.muzzlyworld.genimovie.model.DetailViewState
import com.muzzlyworld.genimovie.model.observeEvent
import com.muzzlyworld.genimovie.util.iloader.fill

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var castAdapter: CastAdapter

    private val model: DetailsViewModel by viewModels()

    private val safeArgs: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContent()

        model.detailMovieViewState.observe(viewLifecycleOwner, Observer {
            render(it)
        })

        model.errorMessage.observeEvent(viewLifecycleOwner){
            Snackbar.make(binding.layout, it , Snackbar.LENGTH_SHORT).show()
        }

        model.loadDetailMovie(safeArgs.movieId ?: throw IllegalStateException("Movie id is`nt provided"))
    }

    private fun setupContent(){
        castAdapter = CastAdapter()
        binding.cast.adapter = castAdapter

        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun render(state: DetailViewState){
        renderLoading(state)
        renderContent(state)
    }

    private fun renderLoading(state: DetailViewState) = with(binding.loading){
        if(state.isLoading) show() else hide()
    }

    private fun renderContent(state: DetailViewState) = state.detailMovie?.let { with(binding){
        title.text = it.title
        releaseDate.text = it.releaseDate
        genresList.text = it.genresList.joinToString(" • ")
        description.text = it.description
        directors.text = it.directorsList.joinToString(", ")

        binding.poster.fill(it.posterUrl?.toUri(), R.drawable.ic_movie, R.drawable.ic_loader)

        castAdapter.cast = it.cast
    } }
}
