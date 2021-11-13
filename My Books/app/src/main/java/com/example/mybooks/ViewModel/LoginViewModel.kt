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
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val sharedPreferences           : SharedPreferences,
    val useCase                     : UseCase
): ViewModel() {
    //private lateinit var useCase : UseCase
    val isLogin                     = MutableLiveData<EnumValidate>()
    var user:User                   = User.getInstance()

    /**
     * REGISTRAR UN USUARIO
     * @param context
     * @param name     nombre del usuario
     * @param pass     contraseña del usuario
     * */
    fun registerUser(context: Context, name: String, pass: String) {
        if (!(listOf(name, pass).validateStrings())) {
            isLogin.postValue(EnumValidate.EMPTY_VAL)
            return
        }
        val user =
            UserEntity(
                id      = 0,
                name    = name,
                pass    = pass,
                photo   = context.resources.getString(R.string.photoDefault))

        GlobalScope.launch(Dispatchers.Main) {
            val sucess = useCase.createUser(user)

            useCase.login(listOf(name, pass))?.let { data ->
                User.getInstance().id       = data.id
                User.getInstance().name     = data.name
                User.getInstance().pass     = data.pass
                User.getInstance().photo    = data.photo

                sharedPreferences.savaDataUser(context, data = data)
            }
            if (sucess != null)
                isLogin.postValue(EnumValidate.SUCESS)
            else
                isLogin.postValue(EnumValidate.ERROR)
        }.start()


    }

    /**
     * @param context
     * @param name     nombre del usuario
     * @param pass     contraseña del usuario
     * */
    fun login(context: Context, name: String, pass: String) {
        if (!(listOf(name, pass).validateStrings())) {
            isLogin.postValue(EnumValidate.EMPTY_VAL)
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val data = useCase.login(listOf(name, pass))?.let { data ->
                user.id     = data.id
                user.name   = data.name
                user.pass   = data.pass
                user.photo  = data.photo
                sharedPreferences.savaDataUser(context, data = data)
            }

            if (data != null) {
                isLogin.postValue(EnumValidate.SUCESS)
            } else
                isLogin.postValue(EnumValidate.ERROR)

        }

    }

    /**
     * @return Devuelve una lista de strings (user,pass)
     * */
    fun isLogin(context: Context): List<String> {
        val data=SharedPreferences.getInstance().getDataUser(context)
        return  data
    }


}