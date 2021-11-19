package com.example.mybooks.View.Book

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Global
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.NameFragments
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterThemesBook
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.View.Content.ContentFragment
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.View.forms.FormsFragment
import com.example.mybooks.ViewModel.BookViewModel
import com.example.mybooks.databinding.FragmentBookBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFragment : Fragment() {

    private lateinit var _binding : FragmentBookBinding
    private val binding get()     = _binding
    private val bookViewModel     : BookViewModel by viewModels()
    private var adapterThemesBook : AdapterThemesBook = AdapterThemesBook()
    private var sizeListThemes    = 0
    private val global            = Global.getInstance()


    /**listener para mostrar las opciones al seleccionar un tema**/
    interface Select {
        fun showOptionsSelect(show: Boolean)
    }

    companion object {
        private lateinit var book : BookEntity
        private var select        : Select?        = null
        private var globalLocal   : NameFragments? = null


        fun setBook(book: BookEntity) {
            this.book = book
        }

        fun getSelect(): Select? {
            return select
        }

        /**PARA SABER DE QUE FRAGMENT PROVIENE (Book,Saved,Search)
         *@param page =El nombre de la pagina el cual esta en el numerado NameFragments
         *  */
        fun setGlobal(page: NameFragments) {
            this.globalLocal = page
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (global != null) {
            global.fragment = globalLocal!!
        }
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        select = object : Select {
            override fun showOptionsSelect(show: Boolean) {
                if (show)
                    binding.contenedorSelect.animAppear(view.context, 800)
                else
                    binding.contenedorSelect.animVanish(view.context, 200)
            }

        }
        global?.view = view

        Glide.with(view.context)
            .load (book.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into (binding.imgBook)

        binding.txtDateBook.text        = book.creation_date
        binding.txtDescriptionBook.text = book.description
        binding.txtTitleBook.text       = book.name

        MenuActivity.getOnScroll()?.showButtonBook(show = true)
        MenuActivity.getOnScroll()?.showToolbar   (show = false)

        binding.scrollBook.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY)
                MenuActivity.getOnScroll()?.scrollChangeButtom(isUp = true)
            else
                MenuActivity.getOnScroll()?.scrollChangeButtom(isUp = false)
        })

        binding.btnInsertTheme.setOnClickListener { view ->
            val type   = "tema"

            val bundle = bundleOf("type" to type)
            FormsFragment.setBook(book)

            Navigation.findNavController(view)
                .navigate(R.id.action_bookFragment_to_formsFragment, bundle)
        }

        binding.reciclerTemas.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        binding.reciclerTemas.setHasFixedSize(true)
        binding.reciclerTemas.adapter = adapterThemesBook
        binding.reciclerTemas.isNestedScrollingEnabled = false
        adapterThemesBook.setCLic(object : AdapterThemesBook.Clic {
            override fun onClic(theme: ThemeEntity, position: Int, view: View) {
                ContentFragment.setTheme(theme)
                val bundle= bundleOf("type" to "book")
                /**SABER QUE PASARA AL FRAGMENT CONTENT Y QUE PROVIENE DE BOOK*/
               // global?.fragment = NameFragments.CONTENTS
                Navigation.findNavController(view)
                    .navigate(R.id.action_bookFragment_to_contentFragment,bundle)
            }

            override fun onClicEdit(theme: ThemeEntity, position: Int, view: View) {
                val type   = "temaedit"

                val bundle = bundleOf("type" to type)
                FormsFragment.setThema(theme)

                Navigation.findNavController(view)
                    .navigate(R.id.action_bookFragment_to_formsFragment, bundle)
            }

        })
        bookViewModel.list.observe(viewLifecycleOwner, { list ->
            adapterThemesBook.setList(list)
            adapterThemesBook.notifyDataSetChanged()
            sizeListThemes = list.size
            if (list.size <= 1) {
                MenuActivity.getOnScroll()?.showButtonBook(show = false)
            } else {
                MenuActivity.getOnScroll()?.showButtonBook(show = true)
            }

        })
        bookViewModel.getThemes(
            book.id_book
        )
        binding.btnDeselectAll.setOnClickListener { view ->
            adapterThemesBook.getEventSelect().deselect()
        }
        binding.btnDelete.setOnClickListener { view ->
            adapterThemesBook.getEventSelect()
                .deleteSelect(
                    view.context,
                    bookViewModel,
                    book.id_book
                )
        }
        bookViewModel.theme.observe(viewLifecycleOwner, { theme ->
            adapterThemesBook.setList(theme)
            adapterThemesBook.notifyDataSetChanged()
        })
        binding.btnAllThemes.setOnClickListener { view ->
            adapterThemesBook.setTypeList(isSavedList = false)
            bookViewModel.getThemes(
                book.id_book
            )
        }
        binding.btnListSavedThemes.setOnClickListener { view ->
            adapterThemesBook.setTypeList(isSavedList = true)
            bookViewModel.getSavedThemes(
                book.id_book
            )
        }
        binding.btnDeleteBook.setOnClickListener {
            showDialog(it.context)
        }
    }

    /**MOSTRAR UN DIALOGO PARA ELIMINAR UN LIBRO*/
    fun showDialog(context: Context) {
        binding.includeDialog.root.animAppear(
            context  = context,
            duration = 1000
        )
        binding.includeDialog.btnAcept.setOnClickListener {
            bookViewModel.deleteBook(
                bookEntity = book
            )
            binding.includeDialog.root.animVanish(
                context  = it.context,
                duration = 200
            )
            activity?.onBackPressed()
        }
        binding.includeDialog.btnCancel.setOnClickListener {
            binding.includeDialog.root.animVanish(
                context  = it.context,
                duration = 200
            )

        }
    }
}