package com.muzzlyworld.genimovie.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muzzlyworld.genimovie.databinding.ItemMovieBinding
import com.muzzlyworld.genimovie.model.MovieShortcut
import com.muzzlyworld.genimovie.util.loadImage
import kotlinx.coroutines.CoroutineScope

class SearchAdapter(private val scope: CoroutineScope) : ListAdapter<MovieShortcut, SearchAdapter.MovieView>(
    DiffCallback()
){

    var onItemClickListener: ((MovieShortcut) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieView =
        MovieView.from(
            parent,
            onItemClickListener,
            scope
        )

    override fun onBindViewHolder(holder: MovieView, position: Int) = holder.bind(getItem(position))

    class MovieView(private val binding: ItemMovieBinding,
                    private val onItemClickListener: ((MovieShortcut) -> Unit)?,
                    private val scope: CoroutineScope
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: MovieShortcut) {
            binding.title.text = item.title
            binding.description.text = item.description
            binding.poster.loadImage(item.posterUrl, scope)
            binding.layout.setOnClickListener { onItemClickListener?.invoke(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, onItemClickListener: ((MovieShortcut) -> Unit)?, scope: CoroutineScope): MovieView {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
                return MovieView(
                    binding,
                    onItemClickListener,
                    scope
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieShortcut>() {

        override fun areItemsTheSame(oldItem: MovieShortcut, newItem: MovieShortcut): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieShortcut, newItem: MovieShortcut): Boolean {
            return oldItem.id == newItem.id
        }
    }
}