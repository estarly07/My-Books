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

    var  list: List<BookEntity> = listOf()
    @SuppressLint("NotifyDataSetChanged")
    fun setlist(list: List<BookEntity>) {
        this.notifyDataSetChanged()
        this.list = list
    }


    private lateinit var click: (book: BookEntity, position: Int, view: View)->Unit
    fun setClick(clic: (book: BookEntity, position: Int, view: View)->Unit) {
        this.click = clic
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
            click.invoke(list[position], position, view)

        }

        holder.binding.txtDateBookH.text = list[position].creation_date
        holder.binding.txtNameBookH.text = list[position].name
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var binding: ItemBookHotizontalBinding) : RecyclerView.ViewHolder(binding.root)
}