package com.example.mybooks.View.qr

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mybooks.databinding.ActivityQrBinding

class QrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)

        setContentView(binding.root)
        (intent.extras?.get("qr")).let {
            if (it != null)
                binding.qr.setImageBitmap(it as Bitmap)
        }

    }


}