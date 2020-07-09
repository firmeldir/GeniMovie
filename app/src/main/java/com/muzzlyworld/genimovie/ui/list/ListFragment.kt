package com.muzzlyworld.genimovie.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.muzzlyworld.genimovie.Injection
import com.muzzlyworld.genimovie.R
import com.muzzlyworld.genimovie.databinding.FragmentListBinding
import com.muzzlyworld.genimovie.model.SearchViewState
import com.muzzlyworld.genimovie.util.model.observeEvent
import com.muzzlyworld.genimovie.ui.list.adapter.SearchAdapter
import com.muzzlyworld.genimovie.util.DebounceTextWatcher

//Todo : Make scrollListener depend from lifecycle scope

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var contentAdapter: SearchAdapter

    private lateinit var viewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory())
            .get(ListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContent()
        setupQuerying()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { render(it) })

        viewModel.errorMessage.observeEvent(viewLifecycleOwner){
            Snackbar.make(binding.layout, it , Snackbar.LENGTH_SHORT).show()
        }

        viewModel.keyboardShowAction.observeEvent(viewLifecycleOwner){
            changeFocusEditText(it)
        }
    }

    private val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            viewModel.loadNextMovies((binding.content.layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
        }
    }

    override fun onStart() {
        super.onStart()
        binding.content.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        super.onStop()
        binding.content.removeOnScrollListener(scrollListener)
    }

    private fun setupContent() = with(binding){
        contentAdapter = SearchAdapter()
            .apply { onItemClickListener = { navigateToDetail(it.id) } }
        content.adapter = contentAdapter

        search.setOnClickListener { viewModel.onSearchClick() }
    }

    private fun setupQuerying(){
        binding.searchInput.addTextChangedListener(DebounceTextWatcher(lifecycleScope) {
            viewModel.searchForQuery(it)
        })
    }

    private fun render(state: SearchViewState){
        renderContent(state)
        renderLoading(state)
        renderButton(state)
    }

    private fun renderContent(state: SearchViewState){
        contentAdapter.submitList(state.searchMovies)
    }

    private fun renderLoading(state: SearchViewState) = with(binding.loading){
        if(state.isLoading && !state.isSearching) show() else hide()
    }

    private fun renderButton(state: SearchViewState) {
        binding.search.setIconResource(
            if(state.isSearching) R.drawable.ic_back_arrow else R.drawable.ic_search
        )
    }

    private fun changeFocusEditText(focus: Boolean){
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { manager ->
            if(focus){
                binding.searchInput.requestFocus()
                manager.showSoftInput(binding.searchInput, 0)
            }else {
                binding.searchInput.setText("")
                requireActivity().currentFocus?.windowToken?.let { manager.hideSoftInputFromWindow(it, 0) }
            }
        }
    }

    private fun navigateToDetail(id: Int){
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment(id.toString())
        findNavController().navigate(action)
    }
}