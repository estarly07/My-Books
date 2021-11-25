package com.example.mybooks.View.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.R
import com.example.mybooks.databinding.ItemBooksavedBinding

class AdapterSavedBooks(val context: Context) :
    RecyclerView.Adapter<AdapterSavedBooks.ViewHolder>() {
    private var list = listOf<BookEntity>()
    val listColors   = listOf(
        R.color.Pink,
        R.color.morado,
        R.color.moradoclaro,
        R.color.azul,
        R.color.azul_claro,
        R.color.naranja,
    )

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<BookEntity>) {
        this.list = list
        indice    = 0
        this.notifyDataSetChanged()
    }

    private lateinit var clic: ((book: BookEntity, position: Int, view: View)->Unit)
    fun setClick(click: ((book: BookEntity, position: Int, view: View)->Unit)) {
        this.clic = click
    }

    class ViewHolder(val binding: ItemBooksavedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent   : ViewGroup,
        viewType : Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemBooksavedBinding.inflate(layoutInflater, parent, false))
    }

    /**NOS AYUDA A INTERCALAR LOS COLORES DEL TABLERO*/
    var indice = 0

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.binding.root.context)
            .load               (list[position].image)
            .diskCacheStrategy  (DiskCacheStrategy.ALL)
            .into               (holder.binding.imgBook)

        holder.binding.txtDateBookH.setText(list[position].creation_date)
        holder.binding.txtNameBookH.setText(list[position].name)
        var view: Drawable = context.resources.getDrawable(R.drawable.tablero)
        view.setTint(context.resources.getColor(listColors[indice]))
        holder.binding.tablero.background = view
        indice++
        if (indice == listColors.size)
            indice = 0
        holder.binding.root.setOnClickListener { view ->
            clic.invoke(
                list[position],
                position,
                view)
        }

    }

    override fun getItemCount(): Int = list.size

}