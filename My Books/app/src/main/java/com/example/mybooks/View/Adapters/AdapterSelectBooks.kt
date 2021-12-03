package com.example.mybooks.View.Adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.R
import com.example.mybooks.databinding.ItemBookselectBinding

class AdapterSelectBooks(private val context:Context,private val listBooks: List<BookEntity>) :
    RecyclerView.Adapter<AdapterSelectBooks.Holder>() {

    val listColors   = listOf(
        R.color.Pink,
        R.color.morado,
        R.color.moradoclaro,
        R.color.azul,
        R.color.azul_claro,
        R.color.naranja,
    )

    class Holder(val binding: ItemBookselectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflate = LayoutInflater.from(parent.context)
        return Holder(ItemBookselectBinding.inflate(inflate, parent, false))
    }
    /**NOS AYUDA A INTERCALAR LOS COLORES DEL TABLERO*/
    var indice = 0
    override fun onBindViewHolder(holder: Holder, position: Int) {

        Glide.with(holder.binding.root.context)
            .load(listBooks[position].image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.imgBook)
        holder.binding.txtNameBookH.text = listBooks[position].name

        var view: Drawable = context.resources.getDrawable(R.drawable.tablero)
        view.setTint(context.resources.getColor(listColors[indice]))
        holder.binding.tablero.background = view
        indice++
        if (indice == listColors.size)
            indice = 0
    }

    override fun getItemCount(): Int = listBooks.size
}