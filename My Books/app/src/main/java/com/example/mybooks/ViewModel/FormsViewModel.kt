package com.example.mybooks.ViewModel

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import com.example.mybooks.R
import com.example.mybooks.showToast
import com.example.mybooks.validateStrings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
@HiltViewModel
class FormsViewModel @Inject constructor(
            var useCase : UseCase
): ViewModel() {
    private val user    = User.getInstance()


    /**INSERTAR UN LIBRO
     *
     * @param context
     * @param data      Lista de strings con el nombre, imagen y descripción de un libro
     * */
    fun insertBook(context: Context, data: List<String>) {
        if (!data.validateStrings()) {
            "Llena los campos".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
            return
        }

        val book = BookEntity(
            id_book         = 0,
            name            = data[0],
            image           = data[1],
            creation_date   = getDateNow(),
            saved           = false,
            description     = data[2],
            fk_id_user      = user.id,
            lastTimeOpen    = ""
        )

        println(user.id)
        GlobalScope.launch(Dispatchers.Main) {
            useCase.insertBook(book)
            "Registro realizado!!".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
        }

    }

    /**ACTUALIZAR UN LIBRO
     *
     * @param context
     * @param data      Lista de strings con el nombre, imagen y descripción de un libro
     * @param bookOld   Libro el que se quiere actualizar
     * */
    fun updateBook(context: Context, data: List<String>, bookOld: BookEntity) {
        if (!data.validateStrings()) {
            "Llena los campos".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
            return
        }
        bookOld.name            = data[0]
        bookOld.image           = data[1]
        bookOld.description     = data[2]

        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateBook(bookOld)
        }

    }

    /**INSERTAR UN TEMA
     *
     * @param context
     * @param data      Lista de strings con el nombre del tema
     * */
    fun insertTheme(context: Context, data: List<String>) {
        if (!data.validateStrings()) {
            "Llena los campos".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
            return
        }
        val theme = ThemeEntity(
            idTheme     = data[0].toInt(),
            name        = data[1],
            importance  = if (data[2].toBoolean()) 1 else 0,
            fk_idBook   = data[3].toInt()
        )
        useCase = UseCase(context)
        GlobalScope.launch(Dispatchers.Main) {
            useCase.insertTheme(theme)
            "Registro realizado!!".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
        }

    }

    /**ACTUALIZAR UN TEMA
     *
     * @param context
     * @param data      Lista de strings con el nombre del tema
     * @param themeOld  Tema que se va a actualizar
     * */
    fun updateTheme(context: Context, data: List<String>, themeOld: ThemeEntity) {
        if (!data.validateStrings()) {
            "Llena los campos".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
            return
        }
        themeOld.name = data[0]

        useCase = UseCase(context)
        GlobalScope.launch(Dispatchers.Main) {
            useCase.updateTheme(themeOld)
            "Registro realizado!!".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
        }

    }

    /**OBTENER LA FECHA ACTUAL*/
    fun getDateNow(): String {
        val formater = SimpleDateFormat("dd/M/yyyy")
        return formater.format(Date())
    }

    /**
     * @param context
     * @param subTitle Subtitulo del contenido
     * @param idTheme  Id del tema donde se almacenara
     * @param data     Lista de TextEntitys
     * @param view     Lista de TextEntitys
     * @param type     Para saber en que navigator está (contenidosearch o el principal)
     * */
    fun insertContent(
        context   :    Context,
        subTitle  :    String,
        idTheme   :    Int,
        data      :    List<TextEntity>,
        view      :    View,
        type      :    String
    ) {
        if (subTitle == "") {
            "Ingresa un subtitulo".showToast(
                context = context,
                Toast.LENGTH_SHORT,
                R.layout.toast_login
            )
        }

        val content     = ContentEntity(
            idContent   = 0,
            subTitle    = subTitle,
            fk_idTheme  = idTheme
        )


        GlobalScope.launch(Dispatchers.Main) {
            val idContent = useCase.insertContent(content)
            data.forEach { data ->
                if (idContent != null) {
                    if (data.content != "") {
                        data.fk_id_content  = idContent.toInt()
                        data.id_text        = 0
                        useCase.insertTextData(data)
                    }
                }

            }
            if (type == "contenidosearch"){
                val bundle = bundleOf("type" to "search")
                Navigation.findNavController(view)
                    .navigate(R.id.action_formsFragment2_to_contentFragment2, bundle)
            }else
                Navigation.findNavController(view)
                    .navigate(R.id.action_formsFragment3_to_contentFragment4)
        }


    }

}