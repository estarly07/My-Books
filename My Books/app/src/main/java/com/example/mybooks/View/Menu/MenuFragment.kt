package com.example.mybooks.View.Menu

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Global
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Models.User
import com.example.mybooks.NameFragments
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterBooksHorizontal
import com.example.mybooks.View.Adapters.AdapterBooksVertical
import com.example.mybooks.View.Book.BookFragment
import com.example.mybooks.View.forms.FormsFragment
import com.example.mybooks.ViewModel.MenuViewModel
import com.example.mybooks.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private lateinit var _binding : FragmentMenuBinding
    private val binding get()     = _binding
    private val menuViewModel     : MenuViewModel by viewModels()
    private var adapter           : AdapterBooksVertical = AdapterBooksVertical()
    private val global            = Global.getInstance()
    private val user              = User.getInstance()

    companion object {
        private  var callBack: (()->Unit)?=null
        fun getCallBack(): ()->Unit {
            return callBack!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(view.context)
            .load(user.photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.img)


        binding.btnInsertBook.setOnClickListener { view ->
            val bundle = bundleOf("type" to "book")
            Navigation.findNavController(view)
                .navigate(R.id.action_menuFragment_to_formsFragment, bundle)

        }
        global?.fragment = NameFragments.MENU
        global?.view     = view
        MenuActivity.getOnScroll()?.showButtonBook(show = false)
        MenuActivity.getOnScroll()?.showToolbar   (show = true)
        binding.scrollAllBooks.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY)
                MenuActivity.getOnScroll()?.scrollChangeToolbar(false)
            else
                MenuActivity.getOnScroll()?.scrollChangeToolbar(true)
        }
        getAllBooks(view.context)
    }


    fun getAllBooks(context: Context) {
        callBack =  {
                menuViewModel.getAllBooks()
                menuViewModel.getRecentsBooks()
                Glide.with(context)
                    .load(user.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.img)
                binding.txtNameUser.text =
                    ("${context.resources.getString(R.string.Hello)}, ${user.name}!!")

        }
        menuViewModel.getAllBooks()
        menuViewModel.getRecentsBooks()
        binding.reciclerLibros.isNestedScrollingEnabled = false
        binding.reciclerLibros.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.reciclerLibros.setHasFixedSize(true)

        binding.txtNameUser.text =
            ("${context.resources.getString(R.string.Hello)}, ${user.name}!!")

        binding.reciclerLibros.adapter = adapter
        adapter.setMenuViewModel(menuViewModel)
        adapter.setClic(object : AdapterBooksVertical.OnClick {
            override fun onClick(book: BookEntity, position: Int, view: View) {
                BookFragment.setBook(book)
                /**INDICAR QUE ESTAMOS EN LA PANTALLA BOOK PERO QUE VENIMOS DEL FRAGMENT MENU*/
                BookFragment.setGlobal(NameFragments.BOOKMENU)
                Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_nav_book)
                menuViewModel.updateDateOpenBook(book.id_book)
            }

            override fun onEditClick(book: BookEntity, position: Int, view: View) {
                val bundle = bundleOf("type" to "bookedit")

                FormsFragment.setBook(book)
                Navigation.findNavController(view)
                    .navigate(R.id.action_menuFragment_to_formsFragment, bundle)
            }
        })
        menuViewModel.listBook.observe(viewLifecycleOwner, { list ->
            visibleWait    (list.isEmpty())
            adapter.setlist(list)
        })

        binding.reciclerLibrosMasVistos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.reciclerLibrosMasVistos.setHasFixedSize(true)
        val adapterBooksMoreViews = AdapterBooksHorizontal()
        binding.reciclerLibrosMasVistos.adapter = adapterBooksMoreViews
        adapterBooksMoreViews.setClick { book, position, view->
                BookFragment.setBook(book)

                /**INDICAR QUE ESTAMOS EN LA PANTALLA BOOK PERO QUE VENIMOS DEL FRAGMENT MENU*/
                BookFragment.setGlobal(NameFragments.BOOKMENU)
                Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_nav_book)
        }
        menuViewModel.listBookRecents.observe(viewLifecycleOwner, { list ->
            adapterBooksMoreViews.setlist(list)
        })
        menuViewModel.bookCreate.observe(viewLifecycleOwner, { book ->
            adapter.setlist(book)
        })

    }

    /**SI NO HAY LIBROS MOSTRAR UNA ANIMACION*/
    private fun visibleWait(showAnim: Boolean) {
        if (showAnim) {
            binding.wait.visibility            = View.VISIBLE
            binding.containerScroll.visibility = View.INVISIBLE
        } else {
            binding.wait.visibility            = View.INVISIBLE
            binding.containerScroll.visibility = View.VISIBLE
        }
    }

}