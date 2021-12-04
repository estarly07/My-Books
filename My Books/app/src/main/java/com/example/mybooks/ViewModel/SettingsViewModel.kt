package com.example.mybooks.ViewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.Service.AlarmService
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Model.socket.ServerSocket
import com.example.mybooks.Model.socket.SocketClient
import com.example.mybooks.Models.User
import com.example.mybooks.R
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.View.settings.SettingsFragment
import com.example.mybooks.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    var useCase         : UseCase
): ViewModel() {
    val countBook       = MutableLiveData<Int>   ()
    val countBookSave   = MutableLiveData<Int>   ()
    val countTheme      = MutableLiveData<Int>   ()
    val countThemeSave  = MutableLiveData<Int>   ()
    val user            = User.getInstance       ()
    var userConnected   = MutableLiveData<String>()
    var dataChange      = MutableLiveData<String>()

    /**
     * CALLBACK PARA EJECUTAR LOS METODOS DE INSERTAR LIBROS,TEMAS, TEXTOS Y CONTENIDOS
     * */
    interface CallBack {
        fun insertBooks     (context: Context, list: List<BookEntity>)
        fun insertThemes    (context: Context,list: List<ThemeEntity>)
        fun insertContents  (context: Context,list: List<ContentEntity>)
        fun insertTexts     (list: List<TextEntity>)
    }

    private val callBack = object : CallBack {
        override fun insertBooks(context: Context, list: List<BookEntity>) {
            MenuActivity.getShowDialogListener()
                .setMensajeDialog(context.resources.getString(R.string.InsertandoData))

            GlobalScope.launch(Dispatchers.Main) {
                list.forEach { book ->
                    useCase.insertBook(book)
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

    /**
     * OBTENER LOS TEXTOS DE FIREBASE
     * */
    private fun getTextFirebase(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.getTextFirebase(context,callBack)
        }
    }

    /**
     * OBTENER EL CONTENIDO DE FIREBASE
     * */
    private fun getContentsFirebase(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.getContentFirebase(context,callBack)
        }
    }

    /**
     * OBTENER LA CANTIDAD DE LIBROS Y TEMAS (GUARDADOS Y SIN GUARDAR)
     * */
    fun getEstaditicas() {
        countTheme.value        = 0
        countThemeSave.value    = 0
        GlobalScope.launch(Dispatchers.Main) {
            countBook.value     = useCase.getCountBook(user.id)
            countBookSave.value = useCase.getCountBookSave(user.id)
            val books           = useCase.getAllBooks(user.id)

            books.forEach { book ->
                countTheme.value     =
                    countTheme.value!!.plus(useCase.getCountTheme(book.id_book))
            }
            books.forEach { book ->
                countThemeSave.value =
                    countThemeSave.value!!.plus(useCase.getCountThemeSave(book.id_book))
            }
        }
    }

    /**
     * ACTUALIZAR FOTO DEL USER
     * @param photo Foto a actualizar
     * */
    fun updatePhoto(photo: String) {
        user.photo = photo
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updatePhoto(photo)
        }
    }

    /**
     * ACTUALIZAR NOMBRE DEL USER
     * @param  name     Nombre nuevo
     * @param  context
     * @return Boolean  true=>Si se actualizo false=>Si no se actualizo
     * */
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

    /**
     * OBTENER LOS TEMAS
     * @param  context
     * */
    fun getThemesFirebase(context: Context) {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.getThemesFirebase(
                context  = context,
                callBack = this@SettingsViewModel.callBack
            )
        }
    }

    /**
     * DESCARGAR TODA LA INFORMACION SUBIDA EN FIREBASE
     * @param  context
     * */
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

    /**
     * GUARDAR EL ESTADO DE LA DESCARGA POR SI HAY UN FALLO DE PARTE DEL USUARIO
     * @param  activity
     * @param  isSuccessDownload Si se termino la sincronizacion (true) y si hubo algún fallo(false)
     * */
    fun saveStateDowloand(activity: Activity, isSuccessDownload: Boolean) {
        useCase.saveStateDowloand(activity = activity, isSuccessDownload = isSuccessDownload)
    }

    /**
     * OBTENER EL ESTADO DE LA DESCARGA
     * */
    fun getStateDowloand(activity: Activity): Boolean {
        return useCase.getStateDowloand(activity = activity)
    }

    /**
     * ACTIVAR EL SERVICIO DE GUARDADO EN LA NUBE CADA 24H
     * @param  context
     * @param  activity
     * @param  isActiveSynchronized Si se quiere activar(True) o desactivar(false)
     * */
    fun setService(
        context              : Context,
        activity             : Activity,
        isActiveSynchronized : Boolean
    ) {
        if (isActiveSynchronized) {
            val today: Calendar = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 8)
            today.set(Calendar.MINUTE,      0)
            today.set(Calendar.SECOND,      0)

            AlarmService.getInstance().setAlarm(
                context = context,
                times   = today.timeInMillis,
                codeId  = 1)
        }
        useCase.activeSincronized(
            activity             = activity,
            isActiveSynchronized = isActiveSynchronized
        )
    }

    fun getActiveSincronized(context: Context): Boolean {
        return useCase.getActiveSincronized(context)
    }

    fun getCountSincronized(context: Context): Int {
        return useCase.getCountSincronized(context)
    }

    /**
     * REGISTRAR UN USUARIO O LOGUEARLO
     *
     * @param data     array que contenga el email y la password del usuario
     * @param isLogin  Loguearse(true) registrarse(false)
     * @param listener Callback para ejecutar el metodo de registrar o loguearse dependiendo de isLogin
     * */
    fun registerOrLoginEmail(
        isLogin  : Boolean,
        data     : List<String>,
        listener : SettingsFragment.RegisterEmail
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
                useCase.loginUserFirebase   (email = data[0], pass = data[1], lister = listener)
            else
                useCase.registerUserFirebase(email = data[0], pass = data[1], lister = listener)
        }


    }

    /**
     * OBTENER EL TOKEN DE AUTENTIFICACION
     * */
    fun getToken(context: Context):String{
        return useCase.getToken(context)
    }
    fun saveToken(context: Context,token:String){
        useCase.saveToken(context, token)
    }
    fun getInfoSocket(context: Context):Map<String,Any>{
        return ServerSocket.getInfoSocket(context)
    }

    fun validateNameRed(context: Context, info: Map<String, Any>) = info["NAME_RED"] == ServerSocket.getInfoSocket(context)["NAME_RED"]

    fun convertToMap(info: String):Map<String, Any>{
        var data = info
        data = data.substring(1, data.length - 1)   //eliminar los corchetes

        val keyValuePairs = data.split(",") //Eliminar las " , "

        val map: MutableMap<String, Any> = mutableMapOf()

        for (pair in keyValuePairs)
        {
            val entry = pair.split("=").toTypedArray() //split the pairs to get key and value
            map[entry[0].trim { it <= ' ' }] =
                entry[1].trim { it <= ' ' } //add them to the hashmap and trim whitespaces
        }
        return  map
    }

    fun initComunicationWithServer(
        context: Context,
        host: String,
        port: Int,
        showListBook:(List<BookEntity>,callback:(List<String>) -> Unit) -> Unit,
        changeView:(Boolean,Array<String>) -> Unit
    ) {
        val socketClient=SocketClient()
        socketClient.setContext(context = context)
        socketClient.setUseCase(useCase = useCase)

        socketClient.initComunicationWithServer(
            host         = host,
            port         = port,
            showListBook = showListBook,
            changeView   = changeView
        )

    }
    fun initServer(changeView:(Boolean,Array<String>) -> Unit){
        val usernameConnected:(String)->Unit={ user ->
            userConnected.postValue(user)
        }

        ServerSocket().initServer(
            usernameConnected = usernameConnected,
            useCase           = useCase,
            changeView        = changeView
        )
    }

}