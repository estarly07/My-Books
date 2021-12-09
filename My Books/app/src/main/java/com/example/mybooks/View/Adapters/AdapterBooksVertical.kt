package com.example.mybooks.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animTraslateToLeft
import com.example.mybooks.View.Animations.animatioTraslateToLeftOrRight
import com.example.mybooks.ViewModel.MenuViewModel
import com.example.mybooks.databinding.ItemBookVerticalBinding

class AdapterBooksVertical() :
    RecyclerView.Adapter<AdapterBooksVertical.ViewHolder>() {
    private var menuViewModel  : MenuViewModel?          = null
    private var list           : MutableList<BookEntity> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setlist(list: List<BookEntity>) {
        this.notifyDataSetChanged()
        this.list = list as MutableList<BookEntity>
    }

    fun setlist(book: BookEntity) {
        this.list.add(book)
        this.notifyDataSetChanged()

    }


    fun setMenuViewModel(menuViewModel: MenuViewModel) {
        this.menuViewModel = menuViewModel
    }

    interface OnClick {
        fun onClick     (book: BookEntity, position: Int, view: View)
        fun onEditClick (book: BookEntity, position: Int, view: View)
    }

    private var clic: OnClick? = null

    fun setClic(clic: OnClick) {
        this.clic = clic
    }

    override fun onCreateViewHolder(
        parent   : ViewGroup,
        viewType : Int
    ): ViewHolder {
        val LayoutInflater =
            LayoutInflater.from(parent.context)

        return ViewHolder(ItemBookVerticalBinding.inflate(LayoutInflater, parent, false))
    }

    var duration=350
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        //ANIMACION
//        duration += 200
//        println("$duration")
//        holder.binding.root.animTraslateToLeft(
//            context = holder.binding.root.context,
//            duration = duration
//        )
//
//        //----------------------------------------

        Glide.with(holder.binding.root.context)
            .load(list[position].image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.imgBookVertical)
        holder.binding.root.setOnClickListener { view ->
            if (clic != null) {
                clic!!.onClick(list[position], position, view)
            }
        }
        holder.binding.btnBookEdit.setOnClickListener { view ->
            if (clic != null) {
                clic!!.onEditClick(list[position], position, view)
            }
        }
        if (list[position].saved) {
            holder.binding.btnBookQuitarGuardar.visibility  = View.VISIBLE
            holder.binding.btnBookGuardar.visibility        = View.GONE
        } else {
            holder.binding.btnBookQuitarGuardar.visibility  = View.GONE
            holder.binding.btnBookGuardar.visibility        = View.VISIBLE
        }

        holder.binding.btnBookGuardar.setOnClickListener { view ->
            if (menuViewModel != null) {
                holder.binding.btnBookQuitarGuardar.playAnimation()
                holder.binding.btnBookQuitarGuardar.visibility  = View.VISIBLE
                holder.binding.btnBookGuardar.visibility        = View.GONE
                menuViewModel!!.updateStateBook(
                    isSaved = true,
                    idBook  = list[position].id_book
                )
            }
        }
        holder.binding.btnBookQuitarGuardar.setOnClickListener { view ->
            if (menuViewModel != null) {
                holder.binding.btnBookQuitarGuardar.visibility  = View.GONE
                holder.binding.btnBookGuardar.visibility        = View.VISIBLE

                menuViewModel!!.updateStateBook(
                    isSaved = false,
                    idBook  = list[position].id_book
                )
            }
        }
        holder.binding.txtDateBookV.setText(list[position].creation_date)
        holder.binding.txtNameBookV.setText(list[position].name)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemBookVerticalBinding) : RecyclerView.ViewHolder(binding.root)
}