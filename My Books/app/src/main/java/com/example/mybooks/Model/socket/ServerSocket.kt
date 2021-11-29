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
import java.io.IOException


class ServerSocket {
    private lateinit var serverSocket : ServerSocket
    private lateinit var socketClient : Socket
    private          val PORT         = 5000
    private lateinit var input        : DataInputStream
    private lateinit var out          : DataOutputStream


    fun initServer() {
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
                    mensaje = input.readUTF()
                    println("cliente desconnect")
                    println(mensaje)
                    out.writeUTF("Hola")

                } catch (e: IOException) {
                    println("cliente desconnect")
                }

                socketClient.close()
            }
        }

    }

    companion object {
        fun getInfoSocket(context: Context): Map<String, Any> {
            val wm       = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wm.connectionInfo
            val ip       = Formatter.formatIpAddress(wifiInfo.ipAddress)
            var nameRed  = wifiInfo.ssid
            //QUITARLE LAS COMILLAS QUE TRAE POR DEFECTO EL NOMBRE ("NAME")
            nameRed      = nameRed.substring(1, nameRed.length - 1)

            return mapOf<String, Any>(
                "HOST"     to ip,
                "PORT"     to 5000,
                "NAME_RED" to nameRed
            )
        }
    }
}