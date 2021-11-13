package com.example.mybooks.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    var useCase         : UseCase
): ViewModel() {

    private val user    = User.getInstance()
    val bookCreate      = MutableLiveData<BookEntity>()
    var listBook        = MutableLiveData<List<BookEntity>>()
    var listBookRecents = MutableLiveData<List<BookEntity>>()


    /**
     * OBTENER TODOS LOS LIBROS Y SE GUARDA EN UNA VARIABLE LIVEDATA
     * */
    fun getAllBooks() {
        CoroutineScope(Dispatchers.Main).launch {
            listBook.value = withContext(Dispatchers.IO) { useCase.getAllBooks(user.id) }!!
        }
    }

    /**
     * OBTENER TODOS LOS LIBROS RECIENTES Y SE GUARDA EN UNA VARIABLE LIVEDATA
     * */
    fun getRecentsBooks() {
        GlobalScope.launch(Dispatchers.Main) {
            listBookRecents.value =
                withContext(Dispatchers.IO) { useCase.getRecentsBooks(getDateNow()) }!!
        }
    }

    /**
     * ACTUALIZAR LA FECHA QUE FUE ABIERTO EL LIBRO
     * */
    fun updateDateOpenBook(idBook: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateDateOpen(date = getDateNow(), idBook = idBook)
        }

    }

    /**
     * OBTENER LA FECHA ACTUAL
     * */
    fun getDateNow(): String {
        val formateDate = SimpleDateFormat("dd/M/yyyy")
        return formateDate.format(Date())
    }

    /**
     * GUARDAR O DESGUARDAR UN LIBRO
     *
     * @param isSaved Guardar(true) o quitar(false)
     * @param idBook  Id del book a guardar
     * */
    fun updateStateBook(isSaved: Boolean, idBook: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateStateBook(if (isSaved) 1 else 0, idBook)

        }
    }

}