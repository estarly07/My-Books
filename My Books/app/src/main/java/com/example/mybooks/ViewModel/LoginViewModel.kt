package com.example.mybooks.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybooks.Model.Entities.UserEntity
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Model.UseCase
import com.example.mybooks.Models.User
import com.example.mybooks.R
import com.example.mybooks.ViewModel.Enums.EnumValidate
import com.example.mybooks.validateStrings
import kotlinx.coroutines.DelicateCoroutinesApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private lateinit var useCase: UseCase
    val isLogin = MutableLiveData<EnumValidate>()
    var user: User = User.getInstance()
    val sharedPreferences = SharedPreferences.getInstance()

    fun registerUser(context: Context, name: String, pass: String) {
        if (!(listOf(name, pass).validateStrings())) {
            isLogin.postValue(EnumValidate.EMPTY_VAL)
            return
        }
        useCase = UseCase(context)
        val user =
            UserEntity(0, name, pass, photo = context.resources.getString(R.string.photoDefault))
        println(user.name)

        GlobalScope.launch(Dispatchers.Main) {
            val sucess = useCase.createUser(user)

            useCase.login(listOf(name, pass))?.let { data ->
                User.getInstance().id = data.id
                User.getInstance().name = data.name
                User.getInstance().pass = data.pass
                println(data.photo)
                User.getInstance().photo = data.photo

                sharedPreferences.savaDataUser(context, data = data)
            }
            if (sucess != null)

                isLogin.postValue(EnumValidate.SUCESS)
            else
                isLogin.postValue(EnumValidate.ERROR)
        }.start()


    }

    @DelicateCoroutinesApi
    fun login(context: Context, name: String, pass: String) {
        if (!(listOf(name, pass).validateStrings())) {
            isLogin.postValue(EnumValidate.EMPTY_VAL)
            return
        }
        useCase = UseCase(context)

        GlobalScope.launch(Dispatchers.Main) {
            val data = useCase.login(listOf(name, pass))?.let { data ->
                user.id = data.id
                user.name = data.name
                user.pass = data.pass
                user.photo = data.photo
                sharedPreferences.savaDataUser(context, data = data)

            }
            if (data != null) {
                isLogin.postValue(EnumValidate.SUCESS)
            } else
                isLogin.postValue(EnumValidate.ERROR)

        }

    }

    fun isLogin(context: Context): List<String> {
        val data=SharedPreferences.getInstance().getDataUser(context)
        return  data
    }


}