package com.example.mybooks.View.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mybooks.databinding.ItemThemeBinding

class AdapterSearch : RecyclerView.Adapter<AdapterSearch.ViewHolder>() {
    private var listResultSearch = mutableListOf<Map<String, String>>()
    fun setList(listResultSearch: MutableList<Map<String, String>>) {
        this.notifyDataSetChanged()
        this.listResultSearch = listResultSearch
    }

    class ViewHolder(val binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layouInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemThemeBinding.inflate(layouInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNameTheme.setText(listResultSearch.get(position).get("name"))
    }

    override fun getItemCount(): Int = listResultSearch.size
}