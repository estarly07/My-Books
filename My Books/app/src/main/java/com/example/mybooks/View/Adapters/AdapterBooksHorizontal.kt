package com.example.mybooks.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.databinding.ItemBookHotizontalBinding

class AdapterBooksHorizontal :
    RecyclerView.Adapter<AdapterBooksHorizontal.ViewHolder>() {

    interface Clic {
        fun clic(book: BookEntity, position: Int, view: View)
    }
     var  list: List<BookEntity> = listOf()
    @SuppressLint("NotifyDataSetChanged")
    fun setlist(list: List<BookEntity>) {
        this.notifyDataSetChanged()
        this.list = list
    }


    private lateinit var clic: Clic
    fun setClic(clic: Clic) {
        this.clic = clic
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):  ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemBookHotizontalBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.binding.root.context)
            .load(list[position].image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.imgBook)
        holder.binding.root.setOnClickListener { view ->
            if (clic != null)
                clic.clic(book = list[position], position, view)
        }

        holder.binding.txtDateBookH.setText(list[position].creation_date)
        holder.binding.txtNameBookH.setText(list[position].name)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var binding: ItemBookHotizontalBinding) : RecyclerView.ViewHolder(binding.root)
}