package com.muzzlyworld.genimovie.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.muzzlyworld.genimovie.R
import com.muzzlyworld.genimovie.databinding.ItemActorBinding
import com.muzzlyworld.genimovie.model.ParticipantActor
import com.muzzlyworld.genimovie.util.iloader.fill

class CastAdapter() : RecyclerView.Adapter<CastAdapter.CastView>(){

    var cast: List<ParticipantActor> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastView  = CastView.from(parent)

    override fun getItemCount(): Int = cast.size

    override fun onBindViewHolder(holder: CastView, position: Int) = holder.bind(cast[position])

    class CastView(private val binding: ItemActorBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ParticipantActor) = with(binding){
            name.text = item.name
            character.text = item.character
            image.fill(item.profileUrl?.toUri(), R.drawable.ic_anonymous, R.drawable.ic_loader)

            executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CastView {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemActorBinding.inflate(layoutInflater, parent, false)
                return CastView(binding)
            }
        }
    }
}