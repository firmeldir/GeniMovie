package com.muzzlyworld.genimovie.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.muzzlyworld.genimovie.Injection
import com.muzzlyworld.genimovie.R
import com.muzzlyworld.genimovie.databinding.FragmentDetailsBinding
import com.muzzlyworld.genimovie.model.DetailsViewState
import com.muzzlyworld.genimovie.util.model.observeEvent
import com.muzzlyworld.genimovie.util.iloader.fill

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var castAdapter: CastAdapter

    private lateinit var viewModel: DetailsViewModel

    private val safeArgs: DetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory())
            .get(DetailsViewModel   ::class.java)
    }

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

        viewModel.viewState.observe(viewLifecycleOwner, Observer { render(it) })

        viewModel.errorMessage.observeEvent(viewLifecycleOwner){
            Snackbar.make(binding.layout, it , Snackbar.LENGTH_SHORT).show()
        }

        viewModel.loadDetailMovie(safeArgs.movieId ?: throw IllegalStateException("Movie id is`nt provided"))
    }

    private fun setupContent() = with(binding){
        castAdapter = CastAdapter()
        cast.adapter = castAdapter

        back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun render(state: DetailsViewState){
        renderContent(state)
        renderLoading(state)
    }

    private fun renderContent(state: DetailsViewState) = state.detailMovie?.let { with(binding){
        title.text = it.title
        releaseDate.text = it.releaseDate
        genresList.text = it.genresList.joinToString(" • ")
        description.text = it.description
        directors.text = it.directorsList.joinToString(", ")

        binding.poster.fill(it.posterUrl?.toUri(), R.drawable.ic_movie, R.drawable.ic_loader)

        castAdapter.cast = it.cast
    } }

    private fun renderLoading(state: DetailsViewState) = with(binding.loading){
        if(state.isLoading) show() else hide()
    }
}
