package com.example.mybooks.View.Search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybooks.Global
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.NameFragments
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterBooksVertical
import com.example.mybooks.View.Adapters.AdapterSearch
import com.example.mybooks.View.Adapters.AdapterThemesBook
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Book.BookFragment
import com.example.mybooks.View.Content.ContentFragment
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.ViewModel.MenuViewModel
import com.example.mybooks.ViewModel.SearchViewModel
import com.example.mybooks.databinding.FragmentSearchBinding
import com.example.mybooks.textChange
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    lateinit var _binding: FragmentSearchBinding
    val binding get()    = _binding
    val searchViewModel  : SearchViewModel by viewModels()
    val menuViewModel    : MenuViewModel by viewModels()
    var global           : Global? = Global.getInstance()

    enum class Filters {
        ALL, BOOKS, THEMES
    }

    private var filter   = Filters.BOOKS
    private var txtInput = ""

    /**ALMACENAR EL TEXTO INGRESADO POR EL USUARIO*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        global?.fragment = NameFragments.MENU
        global?.view     = view
        MenuActivity.getOnScroll()?.showButtonBook(show = false)
        MenuActivity.getOnScroll()?.showToolbar   (show = true)
        binding.edtSearch.textChange { text ->
            txtInput = text
            when (filter) {
                Filters.BOOKS  -> searchViewModel.searchBooks (name = text)
                Filters.THEMES -> searchViewModel.searchThemes(name = text)
                Filters.ALL    -> searchViewModel.searchAll   (name = text)
            }

        }
        val listenerScroll = MenuActivity.getOnScroll()
        binding.scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY) listenerScroll?.scrollChangeToolbar(true)
            else listenerScroll?.scrollChangeToolbar(false)
        })
        binding.recicleResultSearch.setHasFixedSize(true)
        binding.recicleResultSearch.isNestedScrollingEnabled = false
        binding.recicleResultSearch.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        val adapterBooks = AdapterBooksVertical()
        binding.recicleResultSearch.adapter = adapterBooks
        searchViewModel.listSearchBooks.observe(viewLifecycleOwner, { list ->
            if (list.isEmpty()) {
                binding.containerNoSearch.visibility   = View.VISIBLE
                binding.recicleResultSearch.visibility = View.INVISIBLE
            } else {
                val adapter = AdapterBooksVertical()
                adapter.setClic(object : AdapterBooksVertical.OnClick {
                    override fun onClick(book: BookEntity, position: Int, view: View) {
                        BookFragment.setBook(book)
                        /**INDICAR QUE ESTAMOS EN LA PANTALLA BOOK PERO QUE VENIMOS DEL FRAGMENT SEARCH*/
                        BookFragment.setGlobal(NameFragments.BOOKSEARCH)
                        Navigation.findNavController(view)
                            .navigate(R.id.action_searchFragment_to_nav_book)
                        menuViewModel.updateDateOpenBook(book.id_book)

                    }

                    override fun onEditClick(book: BookEntity, position: Int, view: View) {

                    }
                })
                binding.recicleResultSearch.adapter    = adapter
                adapter.setlist(list as MutableList<BookEntity>)
                binding.containerNoSearch.visibility   = View.INVISIBLE
                binding.recicleResultSearch.visibility = View.VISIBLE
            }
        })
        searchViewModel.listSearchThemes.observe(viewLifecycleOwner, { list ->
            if (list.isEmpty()) {
                binding.containerNoSearch.visibility   = View.VISIBLE
                binding.recicleResultSearch.visibility = View.INVISIBLE
            } else {
                val adapter = AdapterThemesBook()
                adapter.setCLic(object : AdapterThemesBook.Clic {
                    override fun onClic(theme: ThemeEntity, position: Int, view: View) {
                        ContentFragment.setTheme(theme = theme)
                        MenuActivity.getOnScroll()?.showToolbar(false)
                        val bundle= bundleOf("type" to "search")
                       // global?.fragment = NameFragments.CONTENTSSEARCH
                        Navigation.findNavController(view)
                            .navigate(R.id.action_searchFragment_to_contentFragment,bundle)
                    }

                    override fun onClicEdit(theme: ThemeEntity, position: Int, view: View) {

                    }

                })
                binding.recicleResultSearch.adapter    = adapter
                adapter.setList(list as MutableList<ThemeEntity>)
                binding.containerNoSearch.visibility   = View.INVISIBLE
                binding.recicleResultSearch.visibility = View.VISIBLE
            }
        })
        searchViewModel.listSearch.observe(viewLifecycleOwner, { list ->
            if (list.isEmpty()) {
                binding.containerNoSearch.visibility   = View.VISIBLE
                binding.recicleResultSearch.visibility = View.INVISIBLE
            } else {
                val adapter = AdapterSearch()
                binding.recicleResultSearch.adapter = adapter
                adapter.setList(list as MutableList<Map<String, String>>)
                binding.containerNoSearch.visibility = View.INVISIBLE
                binding.recicleResultSearch.visibility = View.VISIBLE
            }
        })

        binding.btnFilteBook.setOnClickListener {
            invisbleIndicators()
            filter = Filters.BOOKS
            binding.indicarBook.animAppear(context = view.context, duration = 200)
            searchViewModel.searchBooks(txtInput)
        }
        binding.btnFilteTheme.setOnClickListener {
            invisbleIndicators()
            filter = Filters.THEMES
            binding.indicarTema.animAppear(context = view.context, duration = 200)
            searchViewModel.searchThemes(txtInput)
        }
    }

    /**PONER INVISIBLE LOS INDICADORES DE POSICION*/
    fun invisbleIndicators() {
        binding.indicarBook.visibility = View.INVISIBLE
        binding.indicarTema.visibility = View.INVISIBLE
    }

}