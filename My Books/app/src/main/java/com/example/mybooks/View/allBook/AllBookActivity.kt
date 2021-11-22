package com.example.mybooks.View.allBook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.mybooks.R
import com.example.mybooks.ViewModel.ContentViewModel
import com.example.mybooks.databinding.ActivityAllBookBinding

class AllBookActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAllBookBinding
    val contentViewModel : ContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter=AdapterAllBook()
        binding.viewPager.adapter=adapter
    }
}