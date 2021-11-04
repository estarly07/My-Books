package com.example.mybooks.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import com.example.mybooks.validateStrings
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class MenuViewModel : ViewModel() {

    private val user = User.getInstance()
    val bookCreate = MutableLiveData<BookEntity>()
    var listBook = MutableLiveData<List<BookEntity>>()
    var listBookRecents = MutableLiveData<List<BookEntity>>()

    companion object {
        private var useCase: UseCase? = null
        fun initUseCase(context: Context) {
            if (useCase == null)
                useCase = UseCase(context)
        }
    }


    fun getAllBooks() {

        CoroutineScope(Dispatchers.Main).launch {
            listBook.value = withContext(Dispatchers.IO) { useCase?.getAllBooks(user.id) }!!
        }

    }

    fun getRecentsBooks() {

        GlobalScope.launch(Dispatchers.Main) {
            listBookRecents.value =
                withContext(Dispatchers.IO) { useCase?.getRecentsBooks(getDateNow()) }!!
        }
    }

    fun updateDateOpenBook(idBook: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase?.updateDateOpen(date = getDateNow(), idBook = idBook)
        }

    }

    fun getDateNow(): String {
        val formateDate = SimpleDateFormat("dd/M/yyyy")
        return formateDate.format(Date())
    }

    fun updateStateBook(context: Context, isSaved: Boolean, idBook: Int) {
//        useCase = UseCase(context)
        GlobalScope.launch(Dispatchers.Main) {
            useCase?.updateStateBook(if (isSaved) 1 else 0, idBook)

        }
    }

}