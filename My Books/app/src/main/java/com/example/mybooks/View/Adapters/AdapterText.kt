package com.example.mybooks.View.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.databinding.ItemTextBinding

class AdapterText : RecyclerView.Adapter<AdapterText.Holder>() {
    private var list = listOf<TextEntity>()

    fun setList(list: List<TextEntity>) {
        this.list = list
    }

    interface Clic {
        fun clic(textEntity: TextEntity, positionText: Int, view: View)
    }

    private var clic: Clic? = null
    fun setClic(clic: Clic) {
        this.clic = clic
    }

    class Holder(val binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(ItemTextBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        if (list[position].type == "TEXT") {
            holder.binding.txtContent.setText(list[position].content)
            holder.binding.txtContent.visibility = View.VISIBLE
            holder.binding.txtContent.setOnClickListener {
                clic?.clic(
                    textEntity   = list[position],
                    positionText = position,
                    view         = it)
            }

        } else {
            holder.binding.imgContent.visibility = View.VISIBLE
            Glide.with(holder.binding.root.context)
                .load(list[position].content)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imgContent)
            holder.binding.imgContent.setOnClickListener {
                clic?.clic(
                    textEntity   = list[position],
                    positionText = position,
                    view         = it)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}