package com.example.mybooks.View.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.R
import com.example.mybooks.databinding.ItemImguserBinding

class AdapterImgUser(val context: Context) : RecyclerView.Adapter<AdapterImgUser.ViewHolder>() {

    val list = context.resources.getStringArray(R.array.stickers)

    class ViewHolder(val binding: ItemImguserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemImguserBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.binding.root.context)
            .load               (list[position])
            .placeholder(R.drawable.ic_astronaut_holder)
            .diskCacheStrategy  (DiskCacheStrategy.ALL)
            .into               (holder.binding.imgUser)

        holder.binding.imgUser.setOnClickListener {
            clic?.clic(list[position],view = it)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface Clic{
        fun clic(photo:String,view:View)
    }
    private var clic: Clic? =null
    fun setClic(clic:Clic){
        this.clic=clic
    }
}