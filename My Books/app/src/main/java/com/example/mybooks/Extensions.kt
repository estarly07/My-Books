package com.example.mybooks

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.regex.Pattern


fun String.showToast(context: Context, duration: Int, resource: Int): Unit {
    val toast = Toast(context)
    toast.duration = duration
    val view = LayoutInflater.from(context).inflate(resource, null, false)
    val txtMensage: TextView = view.findViewById(R.id.txtMensageToast)
    txtMensage.text = this
    toast.view = view
    toast.show()
}

fun List<String>.validateStrings(): Boolean {
    this.forEach { date ->
        if (date == "")
            return false
    }
    return true
}

var colors = listOf(
    R.color.naranja,
    R.color.morado,
    R.color.moradoclaro,
    R.color.celeste,
    R.color.celeste2,
    R.color.Pink
)

/**CAMBIO DE COLOR AL DARLE CLIC A ALGUN BOTON DEL TOOLBAR
 * @param listview => fondo,texto
 * */
fun List<View?>.pressedButtonsToolbar(context: Context, isPressed: Boolean): Unit {
    if (isPressed) {
        this[0]?.setBackgroundColor(context.resources.getColor(colors[(colors.indices).random()]))
        var img: ImageView? = this[1] as ImageView?
        img?.setColorFilter(context.resources.getColor(R.color.white))
    } else {
        this[0]?.setBackgroundColor(context.resources.getColor(R.color.white))
        var img: ImageView? = this[1] as ImageView?
        img?.setColorFilter(context.resources.getColor(R.color.black))
    }
}

/**CAMBIO DE COLOR AL SELECCIONAR UN TEMA
 * @param listview => fondo,texto
 * */
fun List<View>.selectTheme(context: Context, isSelect: Boolean) {
    if (isSelect) {
        this[0].setBackgroundColor(context.resources.getColor(R.color.primary))
        val txt = this[1] as TextView
        txt.setTextColor(context.resources.getColor(R.color.white))
    } else {
        this[0].background = context.resources.getDrawable(R.drawable.item_book)
        val txt = this[1] as TextView
        txt.setTextColor(context.resources.getColor(R.color.gris3))
    }
}

fun EditText.textChange(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(txt: Editable?) {
            listener(txt.toString())
        }

    })
}

/**EXTENSION PARA CUANDO SELECCIONE UN EDITTEXT O UN IMAGE VIEW DE LA VISTA DONDE SE INGRESA EL CONTENIDO*/
fun View.selectTypeTextOrImage(context: Context, select: Boolean) {
    if (select) {
        this.background = context.resources.getDrawable(R.drawable.ic_circulo_select)
    } else {
        this.background = context.resources.getDrawable(R.drawable.ic_circulo)
    }
}
/**EXTENSION PARA VALIDAR EL EMAIL*/
fun String.validateEmail(): Boolean {
    val pattern = Pattern.compile(
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    )
    val matcher = pattern.matcher(this)
    return matcher.find()
}
