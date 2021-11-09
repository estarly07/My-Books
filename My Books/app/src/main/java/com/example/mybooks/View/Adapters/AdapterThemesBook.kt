package com.example.mybooks.View.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.View.Book.BookFragment
import com.example.mybooks.ViewModel.BookViewModel
import com.example.mybooks.databinding.ItemThemeBinding
import com.example.mybooks.selectTheme

class AdapterThemesBook() :
    RecyclerView.Adapter<AdapterThemesBook.ViewHolder>() {
    private var list        = mutableListOf<ThemeEntity>()
    private var isSavedList = false //SABER QUE LISTA ES SI TODA O SOLA LA QUE TIENE EL ESTADO DE GUARDADO FILTRO

    fun setList(list: List<ThemeEntity>) {
        listThemesSelect.clear()
        this.list = list as MutableList<ThemeEntity>
    }

    fun setTypeList(isSavedList: Boolean) {
        this.isSavedList = isSavedList
    }

    fun setList(theme: ThemeEntity) {
        this.list.add(theme)
    }

    /**SE GUARDAN LOS IDS DE LOS TEMAS SELECIONADOS A ELIMINAR*/
    private var listThemesSelect = mutableListOf<Int>()

    /**SABER QUE YA INICIO LA SELECCION Y QUE CADA VEZ QUE SELECCIONE OTRO ITEM EVITAR LA ANIMACION DEL CONTENEDOR DE LAS OPCIONES*/
    private var isStartSelection = false

    /**TENER LAS VIEWS DE LOS ITEMS SELECCIONADOS PARA POSTERIORMENTE PODER HACER LA FUNCION DE DESELECCIONAR TODAS*/
    private var listAllViews     = mutableListOf<List<View>>()


    /**CALLBACK PARA DESELECCIONAR TODOS LOS ITEMS O ELIMINARLOS*/
    interface EventSelect {
        fun deselect    ()
        fun selectAll   ()
        fun deleteSelect(context: Context, bookViewModel: BookViewModel, fk_idBook: Int)
    }

    interface Clic {
        fun onClic    (theme: ThemeEntity, position: Int, view: View)
        fun onClicEdit(theme: ThemeEntity, position: Int, view: View)
    }

    private var clic: Clic? = null
    fun setCLic(clic: Clic) {
        this.clic = clic
    }


    private val eventSelect: EventSelect = object : EventSelect {
        override fun deselect() {
            listAllViews.forEach { views ->
                views.selectTheme(
                    context  = views[0].context,
                    isSelect = false
                )
            }
            BookFragment.getSelect()?.showOptionsSelect(show = false)
            isStartSelection = false
            listThemesSelect.clear()
            listAllViews.clear()
        }

        override fun selectAll() {
            listThemesSelect.clear()
            list.forEach { data ->
                listThemesSelect.add(data.idTheme)
            }
        }

        override fun deleteSelect(context: Context, bookViewModel: BookViewModel, fk_idBook: Int) {
            bookViewModel.deleteTheme(
                context     = context,
                ids         = listThemesSelect,
                fk_idBook   = fk_idBook,
                isListSaved = isSavedList
            )
            BookFragment.getSelect()?.showOptionsSelect(show = false)
            isStartSelection = false
            listAllViews.clear()

        }


    }

    fun getEventSelect(): EventSelect {
        return eventSelect
    }


    class ViewHolder(var binding: ItemThemeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemThemeBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.root.setOnClickListener {
            if (!listThemesSelect.isEmpty()) {
                selectTheme(
                    idTheme = list[position].idTheme,
                    holder  =     holder
                )
            }
        }
        holder.binding.txtNameTheme.setText(list.get(position).name)

        if (!listThemesSelect.isEmpty()) {
            listThemesSelect.forEach { id ->
                /**PARA CUANDO HAGA SCROLL SE MUESTRE QUIEN ESTA SELECCIONADO*/
                if (id == list[position].idTheme) {
                    val views = listOf(
                        holder.binding.fondo,
                        holder.binding.txtNameTheme
                    )
                    views.selectTheme(
                        context  = holder.binding.root.context,
                        isSelect = false
                    )
                }

            }

        } else {
            val views = listOf(
                holder.binding.fondo,
                holder.binding.txtNameTheme
            )
            views.selectTheme(
                context  = holder.binding.root.context,
                isSelect = false
            )
        }

        holder.binding.btnSelectTheme.setOnClickListener { view ->
            selectTheme(
                idTheme = list[position].idTheme,
                holder  = holder
            )
        }

        holder.binding.btnThemeEdit.setOnClickListener { view ->
            clic?.onClicEdit(list[position], position, view = view)
        }
        holder.binding.fondo.setOnClickListener { view ->
            if (clic != null)
                if (isStartSelection) {
                    selectTheme(
                        idTheme = list[position].idTheme,
                        holder  = holder
                    )
                } else
                    clic?.onClic(
                        theme      = list[position],
                        position   = position,
                        view       = view)
        }
    }

    fun selectTheme(idTheme: Int, holder: ViewHolder) {
        if (!isStartSelection) {
            isStartSelection = true
            BookFragment.getSelect()?.showOptionsSelect(show = true)
        }
        val views = listOf(
            holder.binding.fondo,
            holder.binding.txtNameTheme
        )
        listAllViews.add(views)

        if (!listThemesSelect.isEmpty()) {

            listThemesSelect.forEach { id ->
                if (id == idTheme) {
                    /**DESELECCIONAR EL TEMA SI YA ESTA SELECCIONADO*/
                    listThemesSelect.remove(idTheme)
                    views.selectTheme(
                        context = holder.binding.root.context,
                        false
                    )
                    if (listThemesSelect.size == 0) {
                        isStartSelection = false
                        BookFragment.getSelect()?.showOptionsSelect(show = false)
                    }

                    return
                }

            }
            listThemesSelect.add(idTheme)
            views.selectTheme(
                context = holder.binding.root.context,
                isSelect = true
            )
        } else {
            listThemesSelect.add(idTheme)
            views.selectTheme(
                context = holder.binding.root.context,
                isSelect = true
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}