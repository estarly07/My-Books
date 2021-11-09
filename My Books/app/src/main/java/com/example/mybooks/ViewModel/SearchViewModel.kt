package com.example.mybooks.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private var listMapResult: MutableList<Map<String, String>> = mutableListOf()

    var listSearch          = MutableLiveData<List<Map<String, String>>>()
    var listSearchBooks     = MutableLiveData<List<BookEntity>>()
    var listSearchThemes    = MutableLiveData<List<ThemeEntity>>()
    val user                = User.getInstance()

    companion object {
        private var useCase: UseCase? = null
        fun initUseCase(context: Context) {
            if (useCase == null) {
                useCase = UseCase(context)
            }
        }
    }

    /**
     * BUSCAR TEMAS Y LIBROS (SON GUARDADOS EN UNA VARIABLE LIVEDATA)
     *
     * @param name Texto a buscar
     * */
    fun searchAll(name: String) {
        GlobalScope.launch(Dispatchers.Main) {
            if (!name.isEmpty()) {
                val response = useCase?.searchBooks(user.id, name = name)
                if (response != null) {
                    for (data in response) {
                        val map = mapOf(
                            "id"   to data.id_book.toString(),
                            "name" to data.name
                        )
                        listMapResult.add(map)
                    }
                }
                val books = useCase?.getAllBooks(user.id)
                if (books != null) {
                    for (book in books) {
                        val responsethemes =
                            useCase?.searchThemes(idBook = book.id_book, name = name)
                        if (responsethemes != null) {
                            for (data in responsethemes) {
                                val map = mapOf(
                                    "id"    to data.idTheme.toString(),
                                    "name"  to data.name
                                )
                                listMapResult.add(map)
                            }
                        }
                    }
                }
            }else{
                listMapResult= mutableListOf()
            }
            listSearch.value = listMapResult
        }
    }

    /**
     * BUSCAR LIBROS (SON GUARDADOS EN UNA VARIABLE LIVEDATA)
     *
     * @param name Libro a buscar
     * */
    fun searchBooks(name: String) {
        GlobalScope.launch(Dispatchers.Main) {
            if (!name.isEmpty()) {
                val list = useCase?.searchBooks(name = name, idUser = user.id)
                listSearchBooks.value = list!!
            } else {
                listSearchBooks.value = listOf()
            }
        }
    }

    /**
     * BUSCAR TEMAS (SON GUARDADOS EN UNA VARIABLE LIVEDATA)
     *
     * @param name Tema a buscar
     * */
    fun searchThemes(name: String) {
        GlobalScope.launch(Dispatchers.Main) {
            if (!name.isEmpty()) {
                val books = useCase?.getAllBooks(user.id)
                if (books != null) {
                    var listThemes = mutableListOf<List<ThemeEntity>>()
                    for (book in books) {
                        val list = useCase?.searchThemes(idBook = book.id_book, name = name)
                        listThemes.add(list ?: listOf())
                    }
                    val listThemeTotal = mutableListOf<ThemeEntity>()
                    listThemes.forEach { list ->
                        list.forEach { theme ->
                            listThemeTotal.add(theme)
                        }
                    }
                    listSearchThemes.value = listThemeTotal
                } else {
                    listSearchThemes.value = listOf()
                }

            } else {
                listSearchThemes.value = listOf()
            }
        }
    }
}