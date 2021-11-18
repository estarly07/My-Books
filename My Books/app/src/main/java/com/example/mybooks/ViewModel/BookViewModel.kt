package com.example.mybooks.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    var useCase : UseCase
) : ViewModel() {
    var list    = MutableLiveData<List<ThemeEntity>>()
    var theme   = MutableLiveData<ThemeEntity>()
    val user    = User.getInstance()


    fun getThemes(fk_idBook: Int) {
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
    fun deleteTheme(ids: MutableList<Int>, fk_idBook: Int, isListSaved: Boolean) {
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
    fun deleteBook(bookEntity: BookEntity) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase?.deleteBook(bookEntity)
        }
    }

    /**
     * OBTENER LOS LIBROS GUARDADOS Y ASIGNARLOS A UNA VARIABLE LIVEDATA
     * @param fk_idBook id del libro que contiene los temas a obtener
     * */
    fun getSavedThemes(fk_idBook: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            list.value = useCase?.getSavedThemes(fk_idBook)
        }
    }

}