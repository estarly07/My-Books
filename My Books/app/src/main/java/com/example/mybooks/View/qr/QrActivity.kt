package com.example.mybooks.View.qr

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.mybooks.Model.socket.ServerSocket
import com.example.mybooks.Model.socket.SocketClient
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.ViewModel.SettingsViewModel
import com.example.mybooks.databinding.ActivityQrBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)

        setContentView(binding.root)
        (intent.extras?.get("qr")).let {
            if (it != null) {
                binding.qr.setImageBitmap(it as Bitmap)
                binding.layoutQr.visibility = View.VISIBLE
                settingsViewModel.initServer()

                settingsViewModel.userConnected.observe(this, { name ->
                    binding.btnConnect.text = "Usuario conectado : $name"
                    val timer = object : CountDownTimer(1000, 2000) {
                        override fun onTick(p0: Long) {

                        }

                        override fun onFinish() {
                            binding.layoutQr           .animVanish(this@QrActivity, duration = 500)
                            binding.layoutSendData.root.animAppear(this@QrActivity, duration = 700)
                        }

                    }
                    timer.start()

                })
            } else {
                settingsViewModel.initComunicationWithServer(host = "192.168.1.1", port = 5000)
                binding.layoutSelectBooks.root.animAppear(this@QrActivity, duration = 500)
            }

        }


    }


}