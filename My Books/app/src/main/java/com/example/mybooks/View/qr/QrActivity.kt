package com.example.mybooks.View.qr

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.socket.ServerSocket
import com.example.mybooks.Model.socket.SocketClient
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterSelectBooks
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.ViewModel.SettingsViewModel
import com.example.mybooks.databinding.ActivityQrBinding
import com.example.mybooks.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

@AndroidEntryPoint
class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        /**LAMBDA PARA MODIFICAR EL TEXTO DE LA NUBE Y EL CONTADOR DE LIBROS O CAMBIAR LA VISTA DE QR A LA DE SENTDATA
         *@param boolean saber si es para cambiar la vista(false) o el texto (true)
         *@param List    una lista de strings =>[mensaje de la nube, contador, cantidad de libros]
         * */
        val changeView: (Boolean, Array<String>) -> Unit = { isChangeText, text ->
            runOnUiThread {
                if (isChangeText) {
                    binding.layoutSendData.txtWaitData.text = text[0]
                    binding.layoutSendData.txtContador.text = "${text[1]}/${text[2]} libros"
                } else {
                    binding.layoutQr.animVanish(this@QrActivity, duration = 500)
                    binding.layoutSendData.root.animAppear(this@QrActivity, duration = 700)
                }
            }
        }

        val showListBook: (List<BookEntity>, callback: (List<String>) -> Unit) -> Unit =
            { list, callback ->
                runOnUiThread {

                    val adapter = AdapterSelectBooks(listBooks = list, context = this)
                    binding.layoutSelectBooks.progress.animVanish(context = this, duration = 500);
                    binding.layoutSelectBooks.reciclerLibros.layoutManager =
                        GridLayoutManager(applicationContext, 2)
                    binding.layoutSelectBooks.reciclerLibros.setHasFixedSize(true)
                    binding.layoutSelectBooks.reciclerLibros.adapter = adapter
                    adapter.notifyDataSetChanged()


                    binding.layoutSelectBooks.all.setOnClickListener {
                        showlayoutSendData(list = adapter.getBooksSelect(), callback = callback)
                    }
                    binding.layoutSelectBooks.only.setOnClickListener {
                        showlayoutSendData(list = adapter.getBooksSelect(), callback = callback)
                    }

                }
            }


        setContentView(binding.root)
        (intent.extras?.get("qr")).let {
            if (it != null) {
                binding.qr.setImageBitmap(it as Bitmap)
                binding.layoutQr.visibility = View.VISIBLE
                settingsViewModel.initServer(
                    changeView         = changeView,
                    finishComunication = {
                        runOnUiThread{
                            "Se finalizo el proceso \nde insertar libros".showToast(this,Toast.LENGTH_SHORT,R.layout.toast_login)
                            onBackPressed()
                        }
                    })

                settingsViewModel.dataChange.observe(this, { data ->
                    binding.layoutSendData.txtWaitData.text = data
                })
                settingsViewModel.userConnected.observe(this, { name ->
                    binding.btnConnect.text = "Usuario conectado : $name"
                })
            } else {
                settingsViewModel.initComunicationWithServer(
                    context = this,
                    host = "192.168.1.1",
                    port = 5000,
                    showListBook = showListBook,
                    changeView = changeView,
                    finishComunication = {
                        runOnUiThread{
                            "Se finalizo el proceso \nde compartir".showToast(this,Toast.LENGTH_SHORT,R.layout.toast_login)
                            onBackPressed()
                        }
                    }
                )
                binding.layoutSelectBooks.root.animAppear(context = this@QrActivity, duration = 500)
            }

        }


    }


    /** MOSTRAR LA VISTA DE layoutSendData PARA QUE EL USUARIO ESPERE MIENTRAS INSERTA LOS LIBROS SELECCIONADOS
     * @param list      listado de los nombres de los libros que selecciono el usuario para enviarselos de respuesta al server
     * @param callback  lambda para enviarle el listado al server
     * */
    fun showlayoutSendData(list: List<String>, callback: (List<String>) -> Unit) {
        //Pasarle al servidor el listado de libros que quiere el user
        callback.invoke(list)
        binding.layoutSendData.root.animAppear(this, duration = 700)
        binding.layoutSelectBooks.root.animVanish(this, duration = 500)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}