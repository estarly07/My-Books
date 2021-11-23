package com.example.mybooks.View.allBook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Models.Content
import com.example.mybooks.ViewModel.BookViewModel
import com.example.mybooks.ViewModel.ContentViewModel
import com.example.mybooks.databinding.ActivityAllBookBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllBookActivity : AppCompatActivity() {
    private lateinit var binding           : ActivityAllBookBinding
    private          val bookViewModel     : BookViewModel by viewModels()
    private          val contentViewModel  : ContentViewModel by viewModels()
                     val map               = mutableMapOf<String, List<Content>>()
    private          var cantThemes        = 0
    private          var totalThemes       = 0
    private          var list              = listOf<ThemeEntity>()


    companion object {
        private lateinit var book: BookEntity

        fun setBook(book: BookEntity) {
            this.book = book
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = AdapterAllBook()
        bookViewModel.getThemes(book.id_book)
        contentViewModel.getContentByID(1)

        //CALLBACK PARA MODIFICAR EL ADAPTER DEL PAGERADAPTER
        val callBack = {
            cantThemes++
            if (cantThemes == totalThemes) {
                adapter.setContent(content = map)
                binding.viewPager.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        bookViewModel.list.observe(this@AllBookActivity, { list ->
            adapter.setThemes(list)
            this.list   = list
            totalThemes = list.size

            println(list.size)
            GlobalScope.launch(Dispatchers.Main) {
                list.forEach {
                    this@AllBookActivity.getContentList(theme = it, callback = callBack)
                }
            }
        })
    }

    /**
     * OBTENER EL LISTADO DE CONTENIDO MEDIANTE EL THEME
     * @param theme
     * @param callback Callback para saber que hacer cuando obtenga la lista
     * */
    fun getContentList(theme: ThemeEntity, callback: () -> Unit) {
        contentViewModel.getContentByIdTheme(idTheme = theme.idTheme) { list ->
            map[theme.name] = list
            if (cantThemes != totalThemes) callback.invoke()
        }
    }

}