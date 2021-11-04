package com.example.mybooks.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContentViewModel : ViewModel() {
    var listContent = MutableLiveData<MutableList<Content>>()

    companion object {
        private lateinit var useCase: UseCase
        fun initUsecase(context: Context) {
            useCase = UseCase(context)
        }
    }


    fun getContentByID(idTheme: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val contents = useCase.getContentById(idTheme)
            val arrayData: MutableList<Content> = mutableListOf()
            for (content in contents) {
                val newContent = Content()
                newContent.fk_idTheme = content.fk_idTheme
                newContent.subTitle = content.subTitle
                newContent.idContent = content.idContent
                newContent.arrayText = useCase.getDataContent(content.idContent)

                arrayData.add(newContent)
                listContent.postValue(arrayData)
            }

        }
    }

    fun updateData(data: String, textEntity: TextEntity) {
        textEntity.content = data
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateData(textEntity)
        }

    }

    fun updateSubtitle(subtitle: String, content: Content) {
        content.subTitle = subtitle
        val contentEntity = ContentEntity(
            idContent = content.idContent,
            subTitle = content.subTitle,
            fk_idTheme = content.fk_idTheme
        )
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateContent(content = contentEntity)
        }
    }

    fun saveTheme(idTheme: Int, saved: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.savedTheme(idTheme, saved)
        }
    }

}