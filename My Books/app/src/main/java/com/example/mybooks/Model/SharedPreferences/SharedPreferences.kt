package com.example.mybooks.Model.SharedPreferences

import android.app.Activity
import android.content.Context
import com.example.mybooks.Models.User

class SharedPreferences {
    val KEY = "preferences"
    val KEY_SINCRONIZED = "sincronized"
    val KEY_ID = "id"
    val TOKEN = "token"

    val ID = "idUser"
    val DESCARGA = "descarga"
    val NAME = "name"
    val PASS = "pass"
    val IS_SINCRONIZED = "issincronized"

    companion object {
        private var sharedPreferences: SharedPreferences? = null
        fun getInstance(): SharedPreferences {
            if (sharedPreferences == null) {
                sharedPreferences = SharedPreferences()
            }

            return sharedPreferences!!
        }

    }


    fun saveStateDescarga(activity: Activity, isSucessDowloand: Boolean) {
        val sharedPreferences = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(DESCARGA, isSucessDowloand)
        editor.apply()
        println(" ssssssssssssssssssssssss ${getStateDowloand(activity)}")
    }

    fun getStateDowloand(activity: Activity): Boolean {
        val sharedPreferences = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("descarga", true)
    }

    /**GUARDAR UN BOOLEANO SI EL USUARIO TIENE ACTIVIDADO LA OPCION DE SINCRONIZAR CON CLOUD*/
    fun activeSincronized(activity: Activity, isActiveSincronized: Boolean) {
        val sharedPreferences = activity.getSharedPreferences(KEY_SINCRONIZED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_SINCRONIZED, isActiveSincronized)
        editor.apply()
    }

    fun getActiveSincronized(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(KEY_SINCRONIZED, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_SINCRONIZED, false)
    }


    fun savaDataUser(context: Context, data: User) {
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(ID, data.id)
        editor.putString(NAME, data.name)
        editor.putString(PASS, data.pass)

        editor.commit()
    }
    fun savaDataName(context: Context, name: String) {
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(NAME, name)

        editor.commit()
    }

    fun saveToken(context: Context, token: String) {
        val preferences = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(TOKEN, token)
        editor.commit()
    }

    fun getToken(context: Context): String {
        val preferences = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        return preferences.getString(TOKEN, "")!!
    }

    fun getIdUser(context: Context): Int {
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)
        return preferences.getInt(ID, 0)
    }

    fun saveCountSincronized(context: Context) {
        val sharedPreferences = context.getSharedPreferences("COUNT", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var value = getCountSincronized(context)
        value += 1
        editor.putInt("COUNT", value)
        editor.commit()
    }

    fun getCountSincronized(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("COUNT", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("COUNT", 0)

    }

    fun getDataUser(context: Context): List<String> {
        val data = mutableListOf<String>()
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)
        preferences.getString(NAME, "")?.let { name -> data.add(name) }
        preferences.getString(PASS, "")?.let { pass -> data.add(pass) }
        return data
    }


}