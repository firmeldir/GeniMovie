package com.muzzlyworld.genimovie.ui.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muzzlyworld.genimovie.R
import com.muzzlyworld.genimovie.databinding.ItemMovieBinding
import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.util.iloader.fill

class SearchAdapter : ListAdapter<MovieShortcut, SearchAdapter.MovieView>(MOVIE_SEARCH_COMPARATOR){

    var onItemClickListener: ((MovieShortcut) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieView =
        MovieView.from(
            parent,
            onItemClickListener
        )

    override fun onBindViewHolder(holder: MovieView, position: Int) = holder.bind(getItem(position))

    class MovieView(private val binding: ItemMovieBinding,
                    private val onItemClickListener: ((MovieShortcut) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: MovieShortcut) = with(binding){
            title.text = item.title
            description.text = item.description
            poster.fill(item.posterUrl?.toUri(), R.drawable.ic_movie, R.drawable.ic_loader)
            layout.setOnClickListener { onItemClickListener?.invoke(item) }

            executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, onItemClickListener: ((MovieShortcut) -> Unit)?): MovieView {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
                return MovieView(binding, onItemClickListener)
            }
        }
    }

    companion object {
        private val MOVIE_SEARCH_COMPARATOR = object : DiffUtil.ItemCallback<MovieShortcut>(){
            override fun areItemsTheSame(oldItem: MovieShortcut, newItem: MovieShortcut): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieShortcut, newItem: MovieShortcut): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}