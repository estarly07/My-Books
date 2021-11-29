package com.example.mybooks.View.settings

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mybooks.Model.socket.ServerSocket
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterImgUser
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.ViewModel.SettingsViewModel
import com.example.mybooks.databinding.FragmentSettingsBinding
import com.example.mybooks.showToast
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap
import androidx.core.app.ActivityCompat




@AndroidEntryPoint
class SettingsFragment : Fragment() {
    lateinit var _binding: FragmentSettingsBinding
    val binding get()    = _binding
    var isEditName       = false//SABER SI ESTA EDITANDO EL NOMBRE
    var isSincronized    =false //Saber si el usuario le dio sincronizar y le salio el dialogo para que ingrese su correo y despues de que se logue si pueda sincronizar
    val settingViewModel : SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    interface CallBack {
        /** OBTENER LA INFORMACION DEL CADA VEZ QUE ENTRA A LOS SETTINGS*/
        fun getData()
    }

    /**CALLBACK PARA MOSTRAR EL DIALOGO DE INGRESAR EL CORREO ELECTRONICO*/
    interface RegisterEmail {
        /**CERRAR EL DIALOGO DE INGRESAR EL CORREO ELECTRONICO*/
        fun dimissDialog(token: String)
        fun alert(message: String)

    }

    val callRegisterEmail = object : RegisterEmail {
        override fun dimissDialog(token: String) {
            settingViewModel.saveToken              (context!!, token = token)
            binding.dialogoRegistrar.root.animVanish(context!!, duration = 200)
            if (isSincronized){
                MenuActivity.getShowDialogListener().showDialog(context!!)
                MenuActivity.getShowDialogListener()
                    .setMensajeDialog(context!!.resources.getString(R.string.eliminandoData))
                settingViewModel.sincronizarData(context!!)
                isSincronized=false
            }else{
                MenuActivity.getActiveSincronized().activeSincronized(true)
            }
        }

        override fun alert(message: String) {
            binding.dialogoRegistrar.progress.animVanish     (context!!, duration = 200)
            binding.dialogoRegistrar.layoutButtons.animAppear(context!!, duration = 200)
            message.showToast(
                context!!,
                duration = Toast.LENGTH_SHORT,
                resource = R.layout.toast_login
            )
        }

    }

    companion object {
        private lateinit var callBack: CallBack
        fun getCallBack(): CallBack {
            return callBack
        }
        private lateinit var generateInfoQr:(Context)->Unit
        fun generateInfoQr():(Context)->Unit{
            return generateInfoQr
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchSave.isChecked=settingViewModel.getActiveSincronized(view.context)
        settingViewModel.getEstaditicas()
        val adapter = AdapterImgUser(view.context)
        binding.reciclerStickers.setHasFixedSize(true)
        binding.reciclerStickers.layoutManager = GridLayoutManager(view.context, 4)
        binding.reciclerStickers.adapter = adapter

        adapter.setClic(object : AdapterImgUser.Clic {
            override fun clic(photo: String, view: View) {
                settingViewModel.updatePhoto(photo)
                binding.dialogo.animVanish(view.context, duration = 200)
                Glide.with(view.context)
                    .load(settingViewModel.user.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.img)
            }

        })
        callBack = object : CallBack {
            override fun getData() {
                settingViewModel.getEstaditicas()
                Glide.with(view.context)
                    .load(settingViewModel.user.photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.img)
                binding.txtNameUser.setText(settingViewModel.user.name)
                isEditName = false
                binding.txtNameUser.isEnabled = isEditName
                binding.btnEdit.setBackgroundResource(R.drawable.ic_edit)
                println()
                if (!MenuActivity.getActiveSincronized().getActiveSincronized())
                    binding.switchSave.isChecked = false

            }

        }
        binding.switchSave.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked)
                if (settingViewModel.getToken(view.context) == "")
                    binding.dialogoRegistrar.root.animAppear(view.context, duration = 1000)
                else
                    MenuActivity.getActiveSincronized().activeSincronized(true)
            else
                MenuActivity.getActiveSincronized().activeSincronized(false)
        }
        binding.dialogoRegistrar.btnCancel.setOnClickListener {
            binding.switchSave.isChecked = false
            isSincronized=false
            binding.dialogoRegistrar.root.animVanish(view.context, duration = 200)
        }
        binding.dialogoRegistrar.txtRegister.setOnClickListener { view ->
            if (binding.dialogoRegistrar.btnAcept.text == view.context.resources.getText(R.string.loginE)) {
                binding.dialogoRegistrar.btnAcept.setText(view.context.resources.getText(R.string.registerE))
                binding.dialogoRegistrar.txtRegister.setText(view.context.resources.getText(R.string.loginEmail))
            } else {
                binding.dialogoRegistrar.btnAcept.setText(view.context.resources.getText(R.string.loginE))
                binding.dialogoRegistrar.txtRegister.setText(view.context.resources.getText(R.string.register))
            }

        }
        binding.dialogoRegistrar.btnAcept.setOnClickListener {
            binding.dialogoRegistrar.progress.animAppear     (it.context, duration = 200)
            binding.dialogoRegistrar.layoutButtons.animVanish(it.context, duration = 200)

            settingViewModel.registerOrLoginEmail(
                isLogin  = binding.dialogoRegistrar.btnAcept.text == view.context.resources.getText(
                    R.string.loginE
                ),
                data     = listOf(
                    binding.dialogoRegistrar.edtEmail.text.trim().toString(),
                    binding.dialogoRegistrar.edtPass.text.trim().toString(),
                ),
                listener = callRegisterEmail
            )
        }

        binding.btnDescargar.setOnClickListener {
            showDialog(it.context)
        }
        binding.btnReadQr.setOnClickListener {
            MenuActivity.getQrLector().invoke()
        }
        binding.btnQr.setOnClickListener {
            MenuActivity.validateLocationPermission().invoke()
        }

        generateInfoQr={context->
            val data      = settingViewModel.getInfoSocket(context=context)
            val writer    = QRCodeWriter ()
            val bitMatrix = writer.encode(data.toString(),BarcodeFormat.QR_CODE,350,350)
            val  width    = bitMatrix.width
            val  heigth   = bitMatrix.height
            val bitmap    = Bitmap.createBitmap(width,heigth,Bitmap.Config.RGB_565)
            for (x in 0 until width){
                for (y in 0 until heigth){
                    bitmap.setPixel(x,y,if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            MenuActivity.generateQr().invoke(bitmap)
        }


        Glide.with(view.context)
            .load(settingViewModel.user.photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.img)

        binding.btnEdit.setOnClickListener {
            if (!isEditName) {
                isEditName = true
                binding.txtNameUser.isEnabled = isEditName
                binding.txtNameUser.requestFocus()
                binding.btnEdit.setBackgroundResource(R.drawable.ic_outline_save_24)
            } else {
                if (settingViewModel.updateNameUser(view.context,binding.txtNameUser.text.toString().trim())) {
                    isEditName = false
                    binding.txtNameUser.isEnabled = isEditName
                    binding.txtNameUser.setText(binding.txtNameUser.text.toString().trim())
                    binding.btnEdit.setBackgroundResource(R.drawable.ic_edit)

                } else {
                    "Debes ingresar tu nombre".showToast(
                        it.context,
                        Toast.LENGTH_SHORT,
                        R.layout.toast_login
                    )
                }
            }
        }


        binding.img.setOnClickListener {
            binding.dialogo.animAppear   (it.context, duration = 1000)
        }
        binding.fondo.setOnClickListener {
            if (binding.dialogo.isVisible)
                binding.dialogo.animVanish(it.context, duration = 200)
        }
        binding.txtNameUser.setText(settingViewModel.user.name)

        settingViewModel.countBook.observe(viewLifecycleOwner, { count ->
            binding.txtCountBook.setText("$count")
        })
        settingViewModel.countBookSave.observe(viewLifecycleOwner, { count ->
            binding.txtCountSaveBooks.setText("$count")
        })
        settingViewModel.countTheme.observe(viewLifecycleOwner, { count ->
            binding.txtCountThemes.setText("$count")
        })
        settingViewModel.countThemeSave.observe(viewLifecycleOwner, { count ->
            binding.txtCountSaveThemes.setText("$count")
        })
    }

    /**MOSTRAR UN DIALOGO PARA PREGUNTAR SI QUIERE ELIMINAR LOS LIBROS AL MOMENTO DE SINCRONIZAR CON LA NUBE*/
    fun showDialog(context: Context) {
        isSincronized = true
        val dialog = Dialog(context, R.style.Theme_AppCompat_Dialog)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_accepdownloand)
        val btnAccept = dialog.findViewById<Button>(R.id.btnAcept)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnAccept.setOnClickListener { view ->


            dialog.dismiss()
            if (settingViewModel.getToken(view.context) == "") {
                binding.dialogoRegistrar.root.animAppear             (view.context, duration = 1000)
                binding.dialogoRegistrar.btnAcept.setOnClickListener {
                    binding.dialogoRegistrar.progress.animAppear     (it.context,   duration = 200)
                    binding.dialogoRegistrar.layoutButtons.animVanish(it.context,   duration = 200)

                    settingViewModel.registerOrLoginEmail(
                        isLogin  = binding.dialogoRegistrar.btnAcept.text == view.context.resources.getText(
                            R.string.loginE
                        ),
                        data     = listOf(

                            binding.dialogoRegistrar.edtEmail.text.trim().toString(),
                            binding.dialogoRegistrar.edtPass.text.trim().toString(),
                        ),
                        listener = callRegisterEmail
                    )
                }

            } else {
                MenuActivity.getShowDialogListener().showDialog(view.context)
                MenuActivity.getShowDialogListener()
                    .setMensajeDialog(view.context.resources.getString(R.string.eliminandoData))
                settingViewModel.sincronizarData(view.context)
            }
        }

        btnCancel.setOnClickListener { view ->
            dialog.dismiss()
            isSincronized=false
        }
        dialog.show()
    }
}