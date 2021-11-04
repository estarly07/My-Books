package com.example.mybooks.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Models.Content
import com.example.mybooks.databinding.ItemContentBinding
import java.util.*

class AdapterContent : RecyclerView.Adapter<AdapterContent.ViewHolder>() {

    private var list: MutableList<Content> = mutableListOf()
    private var clean = false

    /**LIMPIAR LA VISTA PARA QUE NO SE REPITAN ITEMS*/

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Content>) {
        clean = true
        this.list = list as MutableList<Content>
        this.notifyDataSetChanged()
    }

    interface Clic {
        /**
         *@param positionContenido es la posicion del contenido
         *@param positionText es la posicion del item del text
         * */
        fun clic(textEntity: TextEntity, positionText: Int, positionContenido: Int, view: View)
        fun clicSubtitle(content: Content, position: Int, view: View)
    }


    private var clic: Clic? = null
    fun setClic(clic: Clic) {
        this.clic = clic
    }

    fun setObject(content: String, positionContenido: Int, positionText: Int) {
        list[positionContenido].arrayText[positionText].content = content
        this.notifyItemChanged(positionContenido)
    }
    fun setSubtitle(subtitle: String, positionContenido: Int){
        list[positionContenido].subTitle=subtitle
        this.notifyItemChanged(positionContenido)
    }

    class ViewHolder(val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemContentBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtSubtitleContent.text = (list[position].subTitle.uppercase())
        holder.binding.txtSubtitleContent.setOnClickListener {
            clic?.clicSubtitle(content = list[position], position = position, view = it)
        }
        val adapter = AdapterText()
        adapter.setClic(object : AdapterText.Clic {
            override fun clic(textEntity: TextEntity, positionText: Int, view: View) {
                clic?.clic(
                    textEntity = textEntity,
                    positionText = positionText,
                    positionContenido = position,
                    view = view
                )
            }

        })
        adapter.setList(list[position].arrayText)
        holder.binding.reciclerText.setHasFixedSize(true)
        holder.binding.reciclerText.layoutManager = LinearLayoutManager(
            holder.binding.root.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        holder.binding.reciclerText.adapter = adapter

    }

    override fun getItemCount(): Int = list.size

}