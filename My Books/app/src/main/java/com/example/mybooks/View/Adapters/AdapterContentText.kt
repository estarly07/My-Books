package com.example.mybooks.View.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.View.forms.FormsFragment
import com.example.mybooks.databinding.ItemContenttextBinding
import com.example.mybooks.selectTypeTextOrImage
import com.example.mybooks.textChange

class AdapterContentText : RecyclerView.Adapter<AdapterContentText.Holder>() {
    private var list = mutableListOf<TextEntity>()
    private var listSelectItems = mutableListOf<Int>()

    interface Clic {
        fun clic(textEntity: TextEntity, position: Int, view: View)
    }

    private var clic: Clic? = null
    fun setClic(clic: Clic) {
        this.clic = clic
    }


    init {
        FormsFragment.listenDelectEvent(object : FormsFragment.DeleteSelect {
            override fun deleteSelect() {
                listSelectItems.forEachIndexed { index, id ->
                    list.forEach { text ->
                        if (id == text.id_text) {
                            list.remove(text)
                            return@forEachIndexed
                        }
                    }

                }
                listSelectItems.clear()
                this@AdapterContentText.notifyDataSetChanged()

            }

        })
    }

    fun setList(dato: TextEntity) {
        list.add(dato)
        this.notifyItemInserted(list.size)
    }

    fun setObject(content:String,position: Int) {
        list[position].content=content
        this.notifyItemChanged(position)
    }

    fun getList(): List<TextEntity> {
        return list
    }


    class Holder(val binding: ItemContenttextBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(ItemContenttextBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {


        if (list[position].type == "TEXT") {
            if (!listSelectItems.isEmpty()) {
                holder.binding.edit.selectTypeTextOrImage(
                    context = holder.binding.root.context,
                    select = false
                )
                listSelectItems.forEachIndexed { i, data ->
                    if (data == list[position].id_text) {
                        println(data)
                        holder.binding.edit.selectTypeTextOrImage(
                            context = holder.binding.root.context,
                            select = true
                        )
                        return@forEachIndexed
                    }
                }
            } else {
                holder.binding.edit.selectTypeTextOrImage(
                    context = holder.binding.root.context,
                    select = false
                )
            }
            holder.binding.containerEdit.visibility = View.VISIBLE
            holder.binding.containerImg.visibility = View.GONE
            holder.binding.edtContent.setText(list[position].content)

            holder.binding.edtContent.setOnClickListener {
                clic?.clic(textEntity = list[position], position = position, view = it)
            }
        } else {
            if (!listSelectItems.isEmpty()) {
                holder.binding.img.selectTypeTextOrImage(
                    context = holder.binding.root.context,
                    select = false
                )
                listSelectItems.forEachIndexed { i, data ->
                    if (data == list[position].id_text) {
                        println(data)
                        holder.binding.img.selectTypeTextOrImage(
                            context = holder.binding.root.context,
                            select = true
                        )
                        return@forEachIndexed
                    }
                }
            } else {
                holder.binding.img.selectTypeTextOrImage(
                    context = holder.binding.root.context,
                    select = false
                )
            }
            Glide.with(holder.binding.root.context)
                .load(list[position].content)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.imgContent)
            holder.binding.containerImg.visibility = View.VISIBLE
            holder.binding.containerEdit.visibility = View.GONE

            holder.binding.imgContent.setOnClickListener {
                clic?.clic(textEntity = list[position], position = position, view = it)
            }
        }
        holder.binding.btnSelectEdit.setOnClickListener {
            seletcItem(
                context = holder.binding.root.context,
                item = list[position],
                view = holder.binding.edit
            )
        }
        holder.binding.btnSelectImg.setOnClickListener {
            seletcItem(
                context = it.context,
                item = list[position],
                view = holder.binding.img
            )
        }
    }

    private fun seletcItem(context: Context, item: TextEntity, view: View) {
        if (listSelectItems.isEmpty()) {
            listSelectItems.add(item.id_text)
            view.selectTypeTextOrImage(context = context, select = true)
        } else {

            listSelectItems.forEachIndexed { index, id ->
                if (id == item.id_text) {
                    listSelectItems.remove(id)
                    view.selectTypeTextOrImage(context = context, select = false)
                    return
                } else {
                    if (index == listSelectItems.size - 1) {
                        listSelectItems.add(item.id_text)
                        view.selectTypeTextOrImage(context = context, select = true)
                    }
                }

            }

        }
        println(listSelectItems)
        println(list)


    }

    override fun getItemCount(): Int = list.size
}