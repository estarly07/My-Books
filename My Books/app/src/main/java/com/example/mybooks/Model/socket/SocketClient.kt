package com.example.mybooks.Model.socket

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


    fun initComunicationWithServer(host:String,port : Int) {
        //IO Ejecturar en background
        GlobalScope.launch(Dispatchers.IO) {
            val socketClient = Socket(host,port)
                input = DataInputStream(socketClient.getInputStream())
                out = DataOutputStream(socketClient.getOutputStream())
                println("connect")
                var mensaje = ""
                try {
//                    mensaje = input.readUTF()
//                    println("cliente desconnect")
//                    println(mensaje)
                    out.writeUTF("Hola")

                } catch (e: IOException) {
                    println("cliente desconnect")

                }

                socketClient.close()
        }

    }
}