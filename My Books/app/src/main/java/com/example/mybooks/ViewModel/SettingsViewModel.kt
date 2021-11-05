package com.example.mybooks.ViewModel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.Service.AlarmService
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import com.example.mybooks.R
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.View.settings.SettingsFragment
import com.example.mybooks.validateEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SettingsViewModel : ViewModel() {
    val countBook = MutableLiveData<Int>()
    val countBookSave = MutableLiveData<Int>()
    val countTheme = MutableLiveData<Int>()
    val countThemeSave = MutableLiveData<Int>()

    val user = User.getInstance()
    lateinit var useCase: UseCase
    fun setUse(context: Context) {
        useCase = UseCase(context)
    }

    interface CallBack {
        fun insertBooks(context: Context, list: List<BookEntity>)
        fun insertThemes(context: Context,list: List<ThemeEntity>)
        fun insertContents(context: Context,list: List<ContentEntity>)
        fun insertTexts(list: List<TextEntity>)
    }

    private val callBack = object : CallBack {
        override fun insertBooks(context: Context, list: List<BookEntity>) {
            Log.w("TAG", "LOOOOOOOOO")

            MenuActivity.getShowDialogListener()
                .setMensajeDialog(context.resources.getString(R.string.InsertandoData))

            GlobalScope.launch(Dispatchers.Main) {
                list.forEach { book ->
                    useCase.insertBook(book)
                    println(book.name)
                }
                delay(300)
                getThemesFirebase(context)
            }
        }

        override fun insertThemes(context: Context,list: List<ThemeEntity>) {
            GlobalScope.launch(Dispatchers.Main) {
                list.forEach { theme ->
                    useCase.insertTheme(theme)
                }
                getContentsFirebase(context = context )
            }
        }

        override fun insertContents(context: Context,list: List<ContentEntity>) {
            GlobalScope.launch(Dispatchers.Main) {
                list.forEach { content ->
                    println(content.subTitle)
                    useCase.insertContent(content)
                }

                delay(300)
                getTextFirebase(context = context)
            }
        }

        override fun insertTexts(list: List<TextEntity>) {
            GlobalScope.launch {
                list.forEach { text ->
                    useCase.insertTextData(text)
                }
                delay(300)
                MenuActivity.getStateDowloand().setState(isSucessDowloand = true)
                MenuActivity.getShowDialogListener().dimissDialog()
            }
        }

    }

    private fun getTextFirebase(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.getTextFirebase(context,callBack)
        }
    }

    private fun getContentsFirebase(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.getContentFirebase(context,callBack)
        }
    }

    fun getEstaditicas() {
        countTheme.value = 0
        countThemeSave.value = 0
        GlobalScope.launch(Dispatchers.Main) {
            countBook.value = useCase.getCountBook(user.id)
            countBookSave.value = useCase.getCountBookSave(user.id)
            val books = useCase.getAllBooks(user.id)

            books.forEach { book ->
                countTheme.value = countTheme.value!!.plus(useCase.getCountTheme(book.id_book))
            }
            books.forEach { book ->
                countThemeSave.value =
                    countThemeSave.value!!.plus(useCase.getCountThemeSave(book.id_book))
            }
        }
    }

    fun updatePhoto(photo: String) {
        user.photo = photo
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updatePhoto(photo)
        }
    }

    fun updateNameUser(context: Context,name: String): Boolean {
        if (name == "") {
            return false
        }
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateName(name = name)
            user.name = name
            SharedPreferences.getInstance().savaDataName(context, name = name)
        }

        return true
    }

    fun getThemesFirebase(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {

            useCase.getThemesFirebase(context,this@SettingsViewModel.callBack)
        }
    }

    fun sincronizarData(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            MenuActivity.getStateDowloand().setState(isSucessDowloand = false)
            useCase.deleteAll()
            delay(300)
            MenuActivity.getShowDialogListener()
                .setMensajeDialog(context.resources.getString(R.string.DescargandoData))
            useCase.getBooksFirebase(context, callBack)
        }
    }

    fun saveStateDowloand(activity: Activity, isSucessDowloand: Boolean) {
        useCase.saveStateDowloand(activity = activity, isSucessDowloand = isSucessDowloand)
    }

    fun getStateDowloand(activity: Activity): Boolean {

        return useCase.getStateDowloand(activity = activity)
    }

    fun setService(context: Context, activity: Activity, isActiveSincronized: Boolean) {
        if (isActiveSincronized) {
            println(System.currentTimeMillis())

            val today: Calendar = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 8)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            println("${today.timeInMillis} ${today.get(Calendar.DAY_OF_MONTH)}")

            AlarmService.getInstance().setAlarm(context, today.timeInMillis, 1)
        }
        useCase.activeSincronized(activity = activity, isActiveSincronized = isActiveSincronized)
    }

    fun getActiveSincronized(context: Context): Boolean {
        return useCase.getActiveSincronized(context)
    }

    fun getCountSincronized(context: Context): Int {
        return useCase.getCountSincronized(context)
    }

    /**
     *@param data array que contenga el email y la password del usuario
     * */
    fun registerOrLoginEmail(
        isLogin: Boolean,
        data:List<String>,
        listener: SettingsFragment.RegisterEmail
    ) {
        if (data[0].isEmpty() || data[1].isEmpty()) {
            listener.alert(message = "Llena los campos")
            return
        }
        if (!data[0].validateEmail()) {
            listener.alert(message = "Email incorrecto")
            return
        }
        if (data[1].length < 6) {
            listener.alert(message = "La contraseña debe tener \nmas de 6 caracteres")
            return

        }
        GlobalScope.launch(Dispatchers.Main) {
            if (isLogin)
                useCase.loginUserFirebase(email = data[0], pass = data[1], lister = listener)
            else
                useCase.registerUserFirebase(email = data[0], pass = data[1], lister = listener)
        }


    }
    fun getToken(context: Context):String{
        return useCase.getToken(context)
    }
    fun saveToken(context: Context,token:String){
        useCase.saveToken(context, token)
    }
}