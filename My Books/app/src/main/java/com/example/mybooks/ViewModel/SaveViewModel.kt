package com.example.mybooks.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveViewModel : ViewModel() {
    var list = MutableLiveData<List<BookEntity>>()

    companion object {
        private var useCase: UseCase? = null
        fun initUseCase(context: Context) {
            if (useCase == null)
                useCase = UseCase(context)
        }
    }

    /**
     * OBTENER TODOS LOS LIBROS Y SE GUARDA EN UNA VARIABLE LIVEDATA
     * */
    fun getAllBooksSaved() {
        GlobalScope.launch(Dispatchers.Main) {
            list.value = useCase?.getAllBooksSaved()
        }
    }

}