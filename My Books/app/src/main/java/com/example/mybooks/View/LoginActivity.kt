package com.example.mybooks.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterAbout
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animTraslateToLeft
import com.example.mybooks.View.Animations.animVanish

import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.ViewModel.Enums.EnumPagesLogin
import com.example.mybooks.ViewModel.Enums.EnumValidate
import com.example.mybooks.ViewModel.LoginViewModel
import com.example.mybooks.changePager
import com.example.mybooks.databinding.ActivityMainBinding
import com.example.mybooks.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private var page = EnumPagesLogin.START
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init(): Unit {
        getInfoUser()
        binding.splash.root.animAppear(this, duration = 1300)
        binding.btnStart.setOnClickListener { view ->
            page = EnumPagesLogin.LOGIN

            binding.containerLogin.root.animTraslateToLeft(
                context = this,
                duration = 500
            )

        }
        binding.containerLogin.btnRegister.setOnClickListener { view ->
            loginViewModel.registerUser(
                this,
                binding.containerLogin.edtUserRegister.text.trim().toString(),
                binding.containerLogin.edtPassRegister.text.trim().toString()
            )
        }
        loginViewModel.isLogin.observe(this, { option ->
            println("HOLA $option")
            when (option) {
                EnumValidate.EMPTY_VAL -> "Llena todos los campos".showToast(
                    this,
                    Toast.LENGTH_SHORT,
                    R.layout.toast_login
                )
                EnumValidate.ERROR -> "No se encontro el usuario".showToast(
                    this,
                    Toast.LENGTH_SHORT,
                    R.layout.toast_login
                )
                EnumValidate.REGISTRER_SUCESS -> {
                    "Bienvenido".showToast(
                        this,
                        Toast.LENGTH_SHORT,
                        R.layout.toast_login
                    )
                    showAbout()
                }
                EnumValidate.LOGIN_SUCESS -> {
                    "Bienvenido".showToast(
                        this,
                        Toast.LENGTH_SHORT,
                        R.layout.toast_login
                    )

                    val intent = Intent()
                    intent.setClass(this, MenuActivity::class.java)
                    startActivity(intent)

                }
            }

        })
        binding.containerLogin.btnLogin.setOnClickListener { view ->
            loginViewModel.login(
                this,
                binding.containerLogin.edtNameLogin.text.trim().toString(),
                binding.containerLogin.edtPassLogin.text.trim().toString()
            )
        }

        binding.containerLogin.btnTextRegister.setOnClickListener { view ->
            page = EnumPagesLogin.REGISTER
            binding.containerLogin.containerInputsLogin.visibility = View.INVISIBLE
            binding.containerLogin.containerInputsRegister.animAppear(
                context = this,
                duration = 1100
            )
        }
    }

    fun showAbout() {
        binding.about.root.animAppear(this, 1100)

        val contents = resources.getStringArray(R.array.contensAbout)
        val titles = resources.getStringArray(R.array.titlesAbout)

        val adapterAbout = AdapterAbout(
            contents = contents, titles = titles, images = arrayOf(
                R.drawable.ic_one,
                R.drawable.ic_two,
                R.drawable.ic_three,
                R.drawable.ic_four,
            )
        )
        binding.about.viewPager.adapter = adapterAbout
        binding.about.viewPager.changePager { position ->
            if (position < contents.size) {
                binding.about.txtButton.text = resources.getString(R.string.next)
            } else {
                binding.about.txtButton.text = resources.getString(R.string.comenzar)
            }
        }
        binding.about.btnNext.setOnClickListener {
            if (binding.about.viewPager.currentItem < contents.size-1) {
                binding.about.viewPager.currentItem=binding.about.viewPager.currentItem+1
            } else {
                val intent = Intent()
                intent.setClass(this, MenuActivity::class.java)
                startActivity(intent)
            }
        }


        binding.about.btnOmitir.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, MenuActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getInfoUser() {
        val data = loginViewModel.isLogin(this)
        Handler().postDelayed({
            if (data[0] == "") {
                binding.splash.root.animVanish(this, 1000)
                binding.containerStart.animAppear(this, 500)
            } else {
                loginViewModel.login(
                    this,
                    data[0],
                    data[1]
                )
            }
        }, 1000)
    }

    override fun onBackPressed() {
        when (page) {
            EnumPagesLogin.START -> super.onBackPressed()
            EnumPagesLogin.LOGIN -> {
                page = EnumPagesLogin.START
                binding.containerLogin.root.animVanish(this, 200)
                binding.containerStart.animAppear(this, 1100)

            }
            EnumPagesLogin.REGISTER -> {
                page = EnumPagesLogin.LOGIN
                binding.containerLogin.containerInputsRegister.animVanish(this, 200)
                binding.containerLogin.containerInputsLogin.animAppear(this, 1100)
            }
        }

    }
}