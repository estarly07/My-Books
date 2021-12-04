package com.example.mybooks.Model.socket

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket

import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import com.google.gson.Gson
import kotlinx.coroutines.delay
import java.io.IOException


class ServerSocket {
    private lateinit var serverSocket: ServerSocket
    private lateinit var socketClient: Socket
    private val PORT = 5000
    private lateinit var input: DataInputStream
    private lateinit var out: DataOutputStream
    private val user = User.getInstance()

    fun initServer(
        usernameConnected: (String) -> Unit,
        useCase: UseCase,
        changeView: (Boolean, Array<String>) -> Unit
    ) {
        //SERVIDOR INICIADO
        serverSocket = ServerSocket(PORT)

        //IO Ejecturar en background
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                socketClient = serverSocket.accept()
                println("cliente connect")

                input = DataInputStream(socketClient.getInputStream())
                out = DataOutputStream(socketClient.getOutputStream())
                println("cliente connect")
                var mensaje = ""
                try {
                    mensaje = input.read().toString()
                    val changeText = GlobalScope.launch(Dispatchers.Main) {
                        usernameConnected.invoke(mensaje)
                        delay(1500)
                        changeView.invoke(false, arrayOf())
                    }
                    changeText.join()

                    var books = mutableListOf<BookEntity>()
                    val getBooks = GlobalScope.launch(Dispatchers.IO) {
                        books = useCase.getAllBooks(User.getInstance().id) as MutableList<BookEntity>
                        changeView.invoke(
                            true,
                            arrayOf("Compartiendo libros", "0", "${books.size}")
                        )
                        delay(1500)
                    }
                    //ESPERAR A QUE TERMINE LA COURRUTINA
                    getBooks.join()
                    val jsonBook = Gson().toJson(books)
                    println("1 $mensaje")
                    out.writeUTF(jsonBook.toString())


                    changeView.invoke(
                        true,
                        arrayOf("Esperando a que seleccionen los libros", "0", "0")
                    )
                    mensaje = input.read().toString()
                    println("2 $mensaje")
                    var indice = 0

                    //val namesBooks = Gson().fromJson(jsonBook, Array<String>::class.java).asList()
                    val namesBooks = Gson().fromJson("[\"Git\",\"Java\"]", Array<String>::class.java).asList()

                    books.clear()

                    //BUSCAR LOS LIBROS MEDIANTE EL NOMBRE DE ESTOS
                    namesBooks.forEach {
                    val booksFound   = useCase.searchBooks(idUser = user.id, name = it)
                        booksFound.forEach { book->
                            books.add(book)
                        }
                    }


                    books.forEach { book ->
                        val json= Gson().toJson(book)

                        out.writeUTF(json)
                        changeView.invoke(
                            true,
                            arrayOf(
                                "Compartiendo \nel libro => ${book.name}",
                                "${indice + 1}",
                                "${books.size}"
                            )
                        )

                        //esperar que al cliente  inserte el libro
                        input.read().toString()

                        val themes=useCase.getThemes(book.id_book)

                        out.writeUTF(Gson().toJson(themes))

                        //esperar que al cliente inserte los temas
                        input.read().toString()

                        print("ADios")
                        indice++
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    println("cliente desconnect")
                } finally {
                    socketClient.close()
                }

            }
        }

    }

//    private fun convertListToMap(books: List<BookEntity>): Map<String, BookEntity> =
//        books.map { book -> "${book.id_book}" to book }.toMap()


    companion object {
        fun getInfoSocket(context: Context): Map<String, Any> {
            val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wm.connectionInfo
            val ip = Formatter.formatIpAddress(wifiInfo.ipAddress)
            var nameRed = wifiInfo.ssid
            //QUITARLE LAS COMILLAS QUE TRAE POR DEFECTO EL NOMBRE ("NAME")
            nameRed = nameRed.substring(1, nameRed.length - 1)
            println(ip)
            return mapOf<String, Any>(
                "HOST" to ip,
                "PORT" to 5000,
                "NAME_RED" to nameRed
            )
        }
    }
}