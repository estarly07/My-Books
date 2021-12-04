package com.example.mybooks.Model.socket

import android.content.Context
import android.widget.Toast
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import com.example.mybooks.R
import com.example.mybooks.showToast
import com.example.mybooks.validateStrings

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class SocketClient {

    private lateinit var input: DataInputStream
    private lateinit var out: DataOutputStream


    private lateinit var useCase: UseCase
    private lateinit var context: Context

    private var arrayThemes= mutableListOf<ThemeEntity>()

    fun setContext(context: Context) {
        this.context = context
    }

    fun setUseCase(useCase: UseCase) {
        this.useCase = useCase
    }


    fun initComunicationWithServer(
        host: String,
        port: Int,
        showListBook: (List<BookEntity>, (List<String>) -> Unit) -> Unit,
        changeView: (Boolean, Array<String>) -> Unit
    ) {
        //IO Ejecturar en background
        GlobalScope.launch(Dispatchers.IO) {
            val socketClient = Socket(host, port)
            try {
                input = DataInputStream(socketClient.getInputStream())
                out = DataOutputStream(socketClient.getOutputStream())
                println("connect")
                var mensaje = User.getInstance().name
//                    mensaje = input.readUTF()
//                    println("cliente desconnect")
                println(mensaje)
                out.writeUTF(mensaje)

                //obtener los libros que envia el servidor
                mensaje =
                    "[{\"creation_date\":\"04/11/2021\",\"description\":\"Comandos de git\",\"fk_id_user\":1,\"id_book\":1,\"image\":\"https://www.espai.es/blog/wp-content/uploads/2021/05/trabajar-ramas-git.png\",\"lastTimeOpen\":\"01/12/2021\",\"name\":\"Git\",\"saved\":true},{\"creation_date\":\"26/11/2021\",\"description\":\"Conocimientos de java\",\"fk_id_user\":1,\"id_book\":2,\"image\":\"https://besthqwallpapers.com/Uploads/17-2-2020/122068/thumb2-java-glitter-logo-programming-language-grid-metal-background-java-creative.jpg\",\"lastTimeOpen\":\"02/12/2021\",\"name\":\"Java\",\"saved\":false}]"
                input.read()
                val down = CountDownLatch(1)
                val books: List<BookEntity> =
                    Gson().fromJson(mensaje, Array<BookEntity>::class.java).asList()

                //mostrar los libros para seleccionarlos
                var gson = ""
                var cantBooks = 0
                showListBook.invoke(books) { books ->
                    cantBooks = books.size
                    gson = Gson().toJson(books)
                    down.countDown()
                }
                down.await()

                out.writeUTF(gson)

                changeView.invoke(true, arrayOf("Esperando libros", "0", "$cantBooks"))
                for (i in 1..cantBooks) {
                    mensaje = input.read().toString()

                    val book = Gson().fromJson(
                        "{\"creation_date\":\"04/11/2021\",\"description\":\"Comandos de git\",\"fk_id_user\":1,\"id_book\":1,\"image\":\"https://midu.dev/images/wallpapers/javascript-small-horizontal-4k.png\",\"lastTimeOpen\":\"04/12/2021\",\"name\":\"PruebaQr\",\"saved\":true}",
                        BookEntity::class.java
                    )
                    var courrutine = GlobalScope.launch(Dispatchers.Main) {
                        book.id_book = insertaBook(
                            book
                        ).toInt()
                    }
                    println("${book.id_book} ID")
                    changeView.invoke(true, arrayOf("Insertar libro => $mensaje", "${i}", "$cantBooks"))
                    courrutine.join()
                    delay(1000)
                    //notificar al server que ya se inserto el libro
                    out.writeUTF("");


                    mensaje = input.read().toString()
                    arrayThemes = Gson().fromJson(
                        "[{\"fk_idBook\":1,\"idTheme\":1,\"importance\":0,\"name\":\"git add\"},{\"fk_idBook\":1,\"idTheme\":2,\"importance\":0,\"name\":\"git commit\"},{\"fk_idBook\":1,\"idTheme\":3,\"importance\":0,\"name\":\"git push\"},{\"fk_idBook\":1,\"idTheme\":4,\"importance\":0,\"name\":\"git pull\"},{\"fk_idBook\":1,\"idTheme\":5,\"importance\":1,\"name\":\"Amend\"},{\"fk_idBook\":1,\"idTheme\":6,\"importance\":0,\"name\":\"git stash\"},{\"fk_idBook\":1,\"idTheme\":7,\"importance\":0,\"name\":\"git status\"},{\"fk_idBook\":1,\"idTheme\":8,\"importance\":1,\"name\":\"git tag\"},{\"fk_idBook\":1,\"idTheme\":9,\"importance\":1,\"name\":\"Eliminar ramas\"},{\"fk_idBook\":1,\"idTheme\":12,\"importance\":0,\"name\":\"ya\"}]",
                        Array<ThemeEntity>::class.java
                    ).toList() as MutableList<ThemeEntity>

                    arrayThemes.forEach {
                        println(it.idTheme)
                    }

                    courrutine = GlobalScope.launch(Dispatchers.Main) {
                        insertTheme(
                            book.id_book,
                        )
                    }
                    changeView.invoke(true, arrayOf("Insertando temas...", "${i}", "$cantBooks"))
                    courrutine.join()
                    delay(1000)
                    println(mensaje)


                    arrayThemes.forEach {
                        println(it.idTheme)

                        changeView.invoke(true, arrayOf("Insertando contenido al tema ${it.name}", "${i}", "$cantBooks"))
                    }


                }

            } catch (e: IOException) {
                println("cliente desconnect")

            }

            socketClient.close()
        }

    }

    suspend fun insertaBook(data: BookEntity):Long {

        val book = BookEntity(
            id_book = 0,
            name = data.name,
            image = data.image,
            creation_date = getDateNow(),
            saved = false,
            description = data.description,
            fk_id_user = User.getInstance().id,
            lastTimeOpen = ""
        )


        return useCase.insertBook(book)
    }

    fun insertTheme(idBook: Int) {


     arrayThemes.forEach { data ->
            val theme = ThemeEntity(
                idTheme = 0,
                name = data.name,
                importance = 0,
                fk_idBook = idBook
            )
            useCase = UseCase(context)

            data.idTheme= useCase.insertTheme(theme).toInt()
        }
    }

    /**OBTENER LA FECHA ACTUAL*/
    fun getDateNow(): String {
        val formater = SimpleDateFormat("dd/M/yyyy")
        return formater.format(Date())
    }

}