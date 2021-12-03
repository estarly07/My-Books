package com.example.mybooks.Model.socket

import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Models.User

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class SocketClient {

    private lateinit var input: DataInputStream
    private lateinit var out: DataOutputStream


    fun initComunicationWithServer(host: String, port: Int,showListBook:(List<BookEntity>) -> Unit) {
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

                mensaje = "[{\"creation_date\":\"04/11/2021\",\"description\":\"Comandos de git\",\"fk_id_user\":1,\"id_book\":1,\"image\":\"https://www.espai.es/blog/wp-content/uploads/2021/05/trabajar-ramas-git.png\",\"lastTimeOpen\":\"01/12/2021\",\"name\":\"Git\",\"saved\":true},{\"creation_date\":\"26/11/2021\",\"description\":\"Conocimientos de java\",\"fk_id_user\":1,\"id_book\":2,\"image\":\"https://besthqwallpapers.com/Uploads/17-2-2020/122068/thumb2-java-glitter-logo-programming-language-grid-metal-background-java-creative.jpg\",\"lastTimeOpen\":\"02/12/2021\",\"name\":\"Java\",\"saved\":false}]"
                    input.read()

                val books:List<BookEntity> = Gson().fromJson(mensaje,Array<BookEntity>::class.java ).asList()
                showListBook.invoke(books)


            } catch (e: IOException) {
                println("cliente desconnect")

            }

            socketClient.close()
        }

    }
}