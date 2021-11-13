package com.example.mybooks.View.Saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Global
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Models.User
import com.example.mybooks.NameFragments
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterSavedBooks
import com.example.mybooks.View.Book.BookFragment
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.ViewModel.MenuViewModel
import com.example.mybooks.ViewModel.SaveViewModel
import com.example.mybooks.databinding.FragmentSavedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedFragment : Fragment() {
    private lateinit var _binding: FragmentSavedBinding
    private val binding get()    = _binding
    private val saveViewModel    : SaveViewModel by viewModels()
    private val menuViewModel    : MenuViewModel by viewModels()
    private val global           = Global.getInstance()
    private val user             = User.getInstance()

    companion object {
        private lateinit var data: GetDataCallBack
        fun getDataCallBack(): GetDataCallBack {
            return data
        }
    }

    interface GetDataCallBack {
        fun getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(view.context)
            .load(user.photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.img)

        global?.fragment = NameFragments.MENU
        data = object : GetDataCallBack {
            override fun getData() {
                Glide.with(view.context)
                    .load(user.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.img)
                saveViewModel.getAllBooksSaved()
                binding.txtNameUser.text =
                    ("${view.context.resources.getString(R.string.Hello)}, ${user.name}!!")
            }
        }

        val scrollListener = MenuActivity.getOnScroll()
        binding.scrollAllBooks.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY)
                scrollListener?.scrollChangeToolbar(isUp = true)
            else {
                scrollListener?.scrollChangeToolbar(isUp = false)
            }
        })
        binding.txtNameUser.text =
            ("${view.context.resources.getString(R.string.Hello)}, ${user.name}!!")

        binding.reciclerLibros.setHasFixedSize(true)
        binding.reciclerLibros.isNestedScrollingEnabled = false
        binding.reciclerLibros.layoutManager     = GridLayoutManager(view.context, 2)
        val adapterSavedBooks                    = AdapterSavedBooks(view.context)
        binding.reciclerLibros.adapter           = adapterSavedBooks


        global?.view = view
        MenuActivity.getOnScroll()?.showButtonBook(show = false)
        MenuActivity.getOnScroll()?.showToolbar   (show = true)

        adapterSavedBooks.setClick(object : AdapterSavedBooks.Click {
            override fun clic(book: BookEntity, position: Int, view: View) {
                BookFragment.setBook(book)

                /**INDICAR QUE ESTAMOS EN LA PANTALLA BOOK PERO QUE VENIMOS DEL FRAGMENT SAVED*/
                BookFragment.setGlobal(NameFragments.BOOKSAVED)
                Navigation.findNavController(view)
                    .navigate(R.id.action_savedFragment_to_nav_book)

                menuViewModel.updateDateOpenBook(book.id_book)
                menuViewModel.getRecentsBooks()
            }

        })

        saveViewModel.list.observe(viewLifecycleOwner, { list ->
            visibleWait(list.isEmpty())
            adapterSavedBooks.setList(list)
        })


    }

    /**SI NO HAY LIBROS MOSTRAR UNA ANIMACION*/
    private fun visibleWait(visibility: Boolean) {
        if (visibility) {
            binding.wait.visibility            = View.VISIBLE
            binding.containerScroll.visibility = View.INVISIBLE
        }else{
            binding.wait.visibility            = View.INVISIBLE
            binding.containerScroll.visibility = View.VISIBLE
        }
    }
}