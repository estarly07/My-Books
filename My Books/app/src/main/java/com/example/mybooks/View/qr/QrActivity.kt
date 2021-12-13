package com.example.mybooks.View.qr

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterSelectBooks
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.ViewModel.SettingsViewModel
import com.example.mybooks.databinding.ActivityQrBinding
import com.example.mybooks.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrActivity : AppCompatActivity() {

    private lateinit var binding           : ActivityQrBinding
    private          val settingsViewModel : SettingsViewModel by viewModels()
    private lateinit var closeAll          : ()->Unit

    private          val TIME              = 60500
    private          var timer             : CountDownTimer? = null

    companion object{
        private var info : Map<String,Any>?=null
        fun setInfo(info : Map<String,Any>){
            this.info=info
        }

    }
    /**LAMBDA PARA FINALIZAR COMUNICACION CON EL SERVER Y EL USUARIO*/
    val finishCommunication : (msg : String)->Unit = { msg ->
        runOnUiThread{
            msg.showToast(this,Toast.LENGTH_SHORT,R.layout.toast_login)
            onBackPressed()
        }
    }

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
                    if (text[0]=="Esperando a que seleccionen los libros"){
                        startTimeOut(isQr = false)
                    }else{
                        if(timer!=null){
                            timer!!.cancel()
                        }
                    }
                    binding.layoutSendData.txtContador.text = "${text[1]}/${text[2]} libros"
                    binding.layoutSendData.r1.scheduleLayoutAnimation()
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
                    binding.layoutSelectBooks.progress.animVanish(context  = this, duration = 500);
                    binding.layoutSelectBooks.reciclerLibros.layoutManager =
                        GridLayoutManager(applicationContext, 2)
                    binding.layoutSelectBooks.reciclerLibros.setHasFixedSize(true)
                    binding.layoutSelectBooks.reciclerLibros.adapter = adapter
                    adapter.notifyDataSetChanged()
                    binding.layoutSelectBooks.reciclerLibros.scheduleLayoutAnimation()


                    binding.layoutSelectBooks.all.setOnClickListener {
                        val allBooks= mutableListOf<String>()
                        list.forEach {
                            allBooks.add(it.name)
                        }

                        showlayoutSendData(list = allBooks, callback = callback)
                    }
                    binding.layoutSelectBooks.only.setOnClickListener {
                        showlayoutSendData(list = adapter.getBooksSelect(), callback = callback)
                    }

                }
            }

        setContentView(binding.root)
        (intent.extras?.get("qr")).let {
            //si el bitmap no es null
            if (it != null) {
                binding.qr.setImageBitmap(it as Bitmap)
                binding.layoutQr.animAppear(this, duration = 1000)
                binding.qr.      animAppear(this, duration = 1500)
                closeAll= settingsViewModel.initServer(
                    changeView          = changeView,
                    finishCommunication = finishCommunication
                )

                startTimeOut(isQr = true)

                settingsViewModel.dataChange.observe(this, { data ->
                    binding.layoutSendData.txtWaitData.text = data
                })
                settingsViewModel.userConnected.observe(this, { name ->
                    timer!!.cancel()
                    binding.btnConnect.text = "Usuario conectado : $name"
                })
            } else {
                closeAll = settingsViewModel.initComunicationWithServer(
                    context             = this,
                    host = info!!["HOST"] as String,
                    port = (info!!["PORT"].toString()).toInt(),
          //          host                = "192.168.1.1",
            //        port                = 5000,
                    showListBook        = showListBook,
                    changeView          = changeView,
                    finishCommunication = finishCommunication
                )
                binding.layoutSelectBooks.root.animAppear(context = this@QrActivity, duration = 700)
            }

        }


    }

    /** COMENZAR EL TIEMPO DE ESPERA DEL SERVIDOR Y CUAN SE AGOTE SE DEVUELVA A LOS SETTINGS
     * @param isQr Si esta en la vista qr ya que se mostrar el contador en el boton de la vista qr y si no se mostrar en la nube de la vista sendata
     * */
    private fun startTimeOut(isQr: Boolean) {
        timer = object : CountDownTimer(TIME.toLong(),1000){
            override fun onTick(p0: Long) {
               if(isQr){
                   binding.btnConnect.text = "${resources.getText(R.string.noSend)}\n\n${p0/1000} seg"
               }else{
                   binding.layoutSendData.txtWaitData.text = "Esperando a que seleccionen los libros\n${p0/1000} seg"
               }

            }

            override fun onFinish() {
                //validar si se conecto un usuario no se cierre la conexion del servidor
                this@QrActivity.finishCommunication.invoke("Tiempo de espera finalizado")
            }

        }

        timer!!.start()
    }


    /** MOSTRAR LA VISTA DE layoutSendData PARA QUE EL USUARIO ESPERE MIENTRAS INSERTA LOS LIBROS SELECCIONADOS
     * @param list      listado de los nombres de los libros que selecciono el usuario para enviarselos de respuesta al server
     * @param callback  lambda para enviarle el listado al server
     * */
    fun showlayoutSendData(list: List<String>, callback: (List<String>) -> Unit) {
        //Pasarle al servidor el listado de libros que quiere el user
        callback.invoke(list)
        binding.layoutSendData   .root.animAppear(this, duration = 1000)
        binding.layoutSendData.r1.scheduleLayoutAnimation()
        binding.layoutSelectBooks.root.animVanish(this, duration = 500)
    }

    override fun onBackPressed() {
       // super.onBackPressed()
        finish()
        closeAll.invoke()
    }

}