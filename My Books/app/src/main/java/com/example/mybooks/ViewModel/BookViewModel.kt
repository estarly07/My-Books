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

class BookViewModel : ViewModel() {
    var useCase: UseCase? = null
    var list = MutableLiveData<List<ThemeEntity>>()
    var theme = MutableLiveData<ThemeEntity>()
    val user = User.getInstance()


    fun getThemes(context: Context, fk_idBook: Int) {
        useCase = UseCase(context)
        GlobalScope.launch(Dispatchers.Main) {
            list.value = useCase?.getThemes(fk_idBook)
        }
    }

    /**
     *ELIMINAR LOS TEMAS SELECCIONADOS
     *
     * @param isListSaved   Saber que lista obtener despues de eliminar los temas seleccionados (Toda o los guardados)
     * @param fk_idBook     Id del libro del que se le van eliminar los temas
     * @param ids           Listado de ids de los temas a eliminar
     * */
    fun deleteTheme(context: Context, ids: MutableList<Int>, fk_idBook: Int, isListSaved: Boolean) {
        useCase = UseCase(context)

        GlobalScope.launch(Dispatchers.Main) {
            ids.forEach { id ->
                useCase?.deleteTheme(id)
            }
            if (isListSaved)
                list.value = useCase?.getSavedThemes(fk_idBook)
            else
                list.value = useCase?.getThemes(fk_idBook)
        }
    }

    /**
     * @param bookEntity Libro a eliminar
     * */
    fun deleteBook(context: Context, bookEntity: BookEntity) {
        useCase = UseCase(context)
        GlobalScope.launch(Dispatchers.Main) {
            useCase?.deleteBook(bookEntity)
        }
    }

    /**
     * OBTENER LOS LIBROS GUARDADOS Y ASIGNARLOS A UNA VARIABLE LIVEDATA
     * @param fk_idBook id del libro que contiene los temas a obtener
     * */
    fun getSavedThemes(context: Context, fk_idBook: Int) {
        useCase = UseCase(context)
        GlobalScope.launch(Dispatchers.Main) {
            list.value = useCase?.getSavedThemes(fk_idBook)
        }
    }

}