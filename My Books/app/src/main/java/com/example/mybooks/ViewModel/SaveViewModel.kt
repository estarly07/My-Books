package com.example.mybooks.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject constructor(
    var useCase : UseCase
) : ViewModel() {
    var list    = MutableLiveData<List<BookEntity>>()

    /**
     * OBTENER TODOS LOS LIBROS Y SE GUARDA EN UNA VARIABLE LIVEDATA
     * */
    fun getAllBooksSaved() {
        GlobalScope.launch(Dispatchers.Main) {
            list.value = useCase.getAllBooksSaved()
        }
    }

}