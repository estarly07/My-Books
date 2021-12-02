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
import java.io.IOException


class ServerSocket {
    private lateinit var serverSocket : ServerSocket
    private lateinit var socketClient : Socket
    private          val PORT         = 5000
    private lateinit var input        : DataInputStream
    private lateinit var out          : DataOutputStream


    fun initServer(usernameConnected:(String)->Unit,useCase: UseCase) {
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

                    usernameConnected.invoke(mensaje)
                    var books= listOf<BookEntity>()
                    val getBooks= GlobalScope.launch(Dispatchers.IO) { books = useCase.getAllBooks(User.getInstance().id) }
                    //ESPERAR A QUE TERMINE LA COURRUTINA
                    getBooks.join()
                    val mapBook=convertListToMap(books)

                    println("cliente desconnect")

                    println(mensaje)
                    out.writeUTF(mapBook.toString())

                } catch (e: IOException) {
                    println("cliente desconnect")
                }

                socketClient.close()
            }
        }

    }

    private fun convertListToMap(books: List<BookEntity>):Map<String,BookEntity> = books.map {book-> "${book.id_book}" to book}.toMap()


    companion object {
        fun getInfoSocket(context: Context): Map<String, Any> {
            val wm       = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wm.connectionInfo
            val ip       = Formatter.formatIpAddress(wifiInfo.ipAddress)
            var nameRed  = wifiInfo.ssid
            //QUITARLE LAS COMILLAS QUE TRAE POR DEFECTO EL NOMBRE ("NAME")
            nameRed      = nameRed.substring(1, nameRed.length - 1)
            println(ip)
            return mapOf<String, Any>(
                "HOST"     to ip,
                "PORT"     to 5000,
                "NAME_RED" to nameRed
            )
        }
    }
}