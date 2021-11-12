package com.example.mybooks.Model.SharedPreferences

import android.app.Activity
import android.content.Context
import com.example.mybooks.Models.User
import javax.inject.Inject

class SharedPreferences @Inject constructor(){
    private val KEY             = "preferences"
    private val KEY_SINCRONIZED = "sincronized"
    private val KEY_ID          = "id"
    private val TOKEN           = "token"

    private val ID              = "idUser"
    private val DESCARGA        = "descarga"
    private val NAME            = "name"
    private val PASS            = "pass"
    private val IS_SINCRONIZED  = "issincronized"

    companion object {
        private var sharedPreferences: SharedPreferences? = null
        fun getInstance(): SharedPreferences {
            if (sharedPreferences == null) {
                sharedPreferences = SharedPreferences()
            }
            return sharedPreferences!!
        }
    }

    /**
     * GUARDAR EL ESTADO DE LA DESCARGA POR SI HAY UN FALLO DE PARTE DEL USUARIO
     * @param  activity
     * @param  isSuccessDownload Si se termino la sincronizacion (true) y si hubo algún fallo(false)
     * */
    fun saveStateDescarga(activity: Activity, isSuccessDownload: Boolean) {
        val sharedPreferences = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        val editor            = sharedPreferences.edit()

        editor.putBoolean(DESCARGA, isSuccessDownload)
        editor.apply()
    }

    fun getStateDowloand(activity: Activity): Boolean {
        val sharedPreferences = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("descarga", true)
    }

    /**GUARDAR UN BOOLEANO SI EL USUARIO TIENE ACTIVIDADO LA OPCION DE SINCRONIZAR CON CLOUD
     * @param activity
     * @param isActiveSynchronized Activar sincronizacion(true), desactivar(false)
     * */
    fun activeSincronized(activity: Activity, isActiveSynchronized: Boolean) {
        val sharedPreferences = activity.getSharedPreferences(KEY_SINCRONIZED, Context.MODE_PRIVATE)
        val editor            = sharedPreferences.edit()

        editor.putBoolean(IS_SINCRONIZED, isActiveSynchronized)
        editor.apply()
    }

    fun getActiveSincronized(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(KEY_SINCRONIZED, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_SINCRONIZED, false)
    }

    /**GUARDAR LA INFORMACION DEL USUARIO EN LAS PREFENCIAS
     * @param context
     * @param data     id,name y pass
     * */
    fun savaDataUser(context: Context, data: User) {
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)
        val editor      = preferences.edit()

        editor.putInt   (ID,   data.id)
        editor.putString(NAME, data.name)
        editor.putString(PASS, data.pass)

        editor.apply()
    }

    /**GUARDAR EL NOMBRE DEL USUARIO EN LAS PREFENCIAS
     * @param context
     * @param name
     * */
    fun savaDataName(context: Context, name: String) {
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)
        val editor      = preferences.edit()

        editor.putString(NAME, name)

        editor.apply()
    }

    /**GUARDAR EL TOKEN DE LA AUTENTIFICACION
     * @param context
     * @param token
     * */
    fun saveToken(context: Context, token: String) {
        val preferences = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)
        val editor      = preferences.edit()

        editor.putString(TOKEN, token)
        editor.apply()
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
        val editor            = sharedPreferences.edit()
        var value             = getCountSincronized(context)

        value += 1
        editor.putInt("COUNT", value)
        editor.apply()
    }

    fun getCountSincronized(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("COUNT", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("COUNT", 0)

    }

    /**OBTENER LA INFORMACION DEL USUARIO
     * @param  context
     * @return Una lista de strings con el name y el pass
     * */
    fun getDataUser(context: Context): List<String> {
        val data        = mutableListOf<String>()
        val preferences = context.getSharedPreferences(KEY_ID, Context.MODE_PRIVATE)

        preferences.getString(NAME, "")?.let { name -> data.add(name) }
        preferences.getString(PASS, "")?.let { pass -> data.add(pass) }
        return data
    }


}