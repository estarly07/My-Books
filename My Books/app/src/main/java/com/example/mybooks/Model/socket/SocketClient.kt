package com.example.mybooks.Model.socket

import android.content.Context
import android.widget.Toast
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.Content
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
import javax.inject.Inject

class SocketClient @Inject constructor(
    private          var useCase : UseCase
){

    private lateinit var input   : DataInputStream
    private lateinit var out     : DataOutputStream
    private lateinit var context : Context

    private var arrayThemes     = listOf<ThemeEntity>()
    private var arrayContents   = listOf<ContentEntity>()
    private var arrayTexts      = listOf<TextEntity>()
    private var socketClient    : Socket?=null

    fun setContext(context: Context) {
        this.context = context
    }

    fun initComunicationWithServer(
        host                : String,
        port                : Int,
        showListBook        : (List<BookEntity>, (List<String>) -> Unit) -> Unit,
        changeView          : (Boolean, Array<String>) -> Unit,
        finishCommunication : (String)->Unit
    ) {
        //IO Ejecturar en background
        GlobalScope.launch(Dispatchers.IO) {
        val gson = Gson()
            try {
                socketClient = Socket(host, port)
                input        = DataInputStream (socketClient?.getInputStream())
                out          = DataOutputStream(socketClient?.getOutputStream())
                println("connect")
                var mensaje  = User.getInstance().name
                out.writeUTF(mensaje)

                //obtener los libros que envia el servidor
                mensaje   = input.readUTF()

                val down  = CountDownLatch(1)
                val books : List<BookEntity> =
                    Gson().fromJson(mensaje, Array<BookEntity>::class.java).asList()

                //mostrar los libros para seleccionarlos
                var jsonBoks  = ""
                var cantBooks = 0
                showListBook.invoke(books) { books ->
                    cantBooks = books.size
                    jsonBoks  = gson.toJson(books)
                    down.countDown()
                }
                down.await()

                out.writeUTF(jsonBoks)

                changeView.invoke(true, arrayOf("Esperando libros", "0", "$cantBooks"))
                for (i in 1..cantBooks) {
                    //obtener el libro que envia el server
                    mensaje = input.readUTF()

                    val book = gson.fromJson(
                        mensaje,
                        BookEntity::class.java
                    )
                    var courrutine   = GlobalScope.launch(Dispatchers.Main) {
                        book.id_book = insertaBook(
                            book
                        ).toInt()
                    }
                    println("${book.id_book} ID")
                    changeView.invoke(true, arrayOf("Insertar libro => ${book.name}", "${i}", "$cantBooks"))
                    courrutine.join()
                    delay(1000)
                    //notificar al server que ya se inserto el libro
                    out.writeUTF("");

                    //obtener los temas que envia el server
                    mensaje     = input.readUTF()
                    arrayThemes = gson.fromJson(
                        mensaje,
                        Array<ThemeEntity>::class.java
                    ).toList() as MutableList<ThemeEntity>

                    courrutine = GlobalScope.launch(Dispatchers.Main) {
                        insertTheme(
                            book.id_book,
                        )
                    }
                    changeView.invoke(true, arrayOf("Insertando temas...", "${i}", "$cantBooks"))
                    courrutine.join()
                    delay(1000)
                    println(mensaje)

                    //ENVIARLE AL SERVIDOR LA LISTA DE TEMAS CON LOS NUEVOS IDS
                    out.writeUTF(Gson().toJson(arrayThemes))

                    arrayThemes.forEachIndexed{index,themeEntity->
                        println(themeEntity.idTheme)


                        //esperar cual es el id del tema (Ya que al insertarlo en el cliente el tema obtiene un nuveo id)
                       val idTheme = input.readUTF().toInt()
                        //esperar el array de contents
                        arrayContents = gson.fromJson(input.readUTF(), Array<ContentEntity>::class.java).toList()

                        courrutine = GlobalScope.launch(Dispatchers.Main) {
                            insertContents(
                                idTheme,
                            )
                        }
                        changeView.invoke(true, arrayOf("Insertando contenido al tema ${themeEntity.name}", "${i}", "$cantBooks"))
                        courrutine.join()
                        delay(1000)
                        println(mensaje)


                        //DECIRLE QUE YA SE INSERTO LOS CONTENIDOS Y ENVIARLE AL SERVIDOR LA LISTA DE LOS CONTENIDOS CON LOS NUEVOS IDS
                        out.writeUTF(gson.toJson(arrayContents))

                        arrayContents.forEachIndexed { index, contentEntity ->

                            //obtener cual es el id del contenido (Ya que al insertarlo en el cliente el tema obtiene un nuveo id)
                           val idContent= input.readUTF().toInt()
                            //obtener el array de texts
                            arrayTexts= gson.fromJson(input.readUTF(),Array<TextEntity>::class.java).toList()
                            courrutine = GlobalScope.launch(Dispatchers.Main) {
                                insertTexts(
                                    idContent,
                                )
                            }
                            changeView.invoke(true, arrayOf("Insertando data...", "${i}", "$cantBooks"))
                            courrutine.join()
                            delay(1000)
                            println(mensaje)


                            //DECIRLE QUE YA SE INSERTO LOS TEXTOS
                            out.writeUTF("")
                        }


                    }

                }

            } catch (e: IOException) {
                println("cliente desconnect")

            }finally {
                finishCommunication.invoke( "Se finalizó comunicación \ncon el sevidor")

            }


        }

    }
    fun closeConnection()=socketClient?.close()

    suspend fun insertaBook(data: BookEntity):Long {

        val book = BookEntity(
            id_book       = 0,
            name          = data.name,
            image         = data.image,
            creation_date = getDateNow(),
            saved         = false,
            description   = data.description,
            fk_id_user    = User.getInstance().id,
            lastTimeOpen  = ""
        )


        return useCase.insertBook(book)
    }

    fun insertTheme(idBook: Int) {

     arrayThemes.forEach { data ->
            val theme = ThemeEntity(
                idTheme     = 0,
                name        = data.name,
                importance  = 0,
                fk_idBook   = idBook
            )
            data.idTheme    = useCase.insertTheme(theme).toInt()
        }
    }
    fun insertContents(themeId: Int) {
         arrayContents.forEach { data ->
             val content     = ContentEntity(
                 idContent   = 0,
                 subTitle    = data.subTitle,
                 fk_idTheme  = themeId
             )
             data.idContent= useCase.insertContent(contentEntity = content).toInt()
         }
    }
    suspend fun insertTexts(contentId: Int) {
         arrayTexts.forEach { data ->
             val text= TextEntity(
                  id_text       = 0,
                  content       = data.content,
                  type          = data.type,
                  fk_id_content = contentId

             )
             useCase.insertTextData(text)
         }
    }

    /**OBTENER LA FECHA ACTUAL*/
    fun getDateNow(): String {
        val formater = SimpleDateFormat("dd/M/yyyy")
        return formater.format(Date())
    }

}