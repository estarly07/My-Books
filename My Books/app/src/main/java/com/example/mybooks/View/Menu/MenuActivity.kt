package com.example.mybooks.View.Menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.example.mybooks.*
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animTraslateToBottomOrUp
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.View.Book.BookFragment
import com.example.mybooks.View.Saved.SavedFragment
import com.example.mybooks.View.allBook.AllBookActivity
import com.example.mybooks.View.qr.QrActivity
import com.example.mybooks.View.settings.SettingsFragment
import com.example.mybooks.ViewModel.SettingsViewModel
import com.example.mybooks.databinding.ActivityMenuBinding
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    lateinit var binding          : ActivityMenuBinding
    lateinit var buttonsToolbar   : List<List<View?>>
    private  val global           = Global.getInstance()
    private  val settingsViewModel: SettingsViewModel by viewModels()

    interface ShowDialog {
        fun showDialog      (context: Context)
        fun dimissDialog    ()
        fun setMensajeDialog(msg: String)
    }

    interface OnScroll {
        /**ESCONDER O MOSTRAR CON UNA ANIMACION EL TOOLBAR DEL MENU*/
        fun scrollChangeToolbar(isUp: Boolean)

        /**ESCONDER O MOSTRAR CON UNA ANIMACION EL BOTON DE LA VISTA DEL BOOK*/
        fun scrollChangeButtom(isUp: Boolean)

        /**DESAPARECER O MOSTRAR TOOLBAR*/
        fun showToolbar(show: Boolean)

        /**DESAPARECER O MOSTRAR EL BOTON*/
        fun showButtonBook(show: Boolean)
    }

    /**CALLBACK PARA GUARADAR Y OBTENER EL ESTADO DE LA DESCARGA DE LA NUBE*/
    interface StateDowloand {
        fun setState        (isSucessDowloand: Boolean)
        fun getStateDowloand()
    }

    interface StateSincronized {
        fun activeSincronized   (isActiveSincronized: Boolean)
        fun getActiveSincronized(): Boolean
    }

    companion object {

        private lateinit var readQr:(()->Unit)
        fun getQrLector():()->Unit{
            return  readQr
        }

        private lateinit var generateQr:((Bitmap)->Unit)
        fun generateQr():(Bitmap)->Unit{
            return  generateQr
        }

        private var showDialog: ShowDialog? = null

        fun getShowDialogListener(): ShowDialog {
            return showDialog!!
        }

        private var onScroll: OnScroll? = null
        fun getOnScroll(): OnScroll? {
            return onScroll
        }


        private lateinit var stateDowloand: StateDowloand
        fun getStateDowloand(): StateDowloand {
            return stateDowloand
        }

        private lateinit var activeSincronized: StateSincronized
        fun getActiveSincronized(): StateSincronized {
            return activeSincronized
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                "Cancelado".showToast(this,Toast.LENGTH_SHORT,R.layout.toast_login)
            } else {
                "El valor escaneado es: " + result.contents.showToast(this,Toast.LENGTH_SHORT,R.layout.toast_login)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.count.setText(
            "${settingsViewModel.getCountSincronized(this)}"
        )
       readQr={
            val intent=IntentIntegrator(this)
            intent.setPrompt("Lee el código Qr para obtener la información")
            intent.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intent.initiateScan()

        }
       generateQr={bitmap->
           val intent =Intent()
           intent.setClass   (this,QrActivity::class.java)
           intent.putExtra("qr",bitmap)
           startActivity     (intent)

        }

        binding.btnShowBook.setOnClickListener {
          val intent = Intent()
          intent.setClass(this@MenuActivity, AllBookActivity::class.java)
          AllBookActivity.setBook(book = BookFragment.getBook())
          startActivity(intent)
      }

        buttonsToolbar = listOf(
            listOf(binding.btnHome,     binding.imgHome),
            listOf(binding.btnSaved,    binding.imgSaved),
            listOf(binding.btnSearch,   binding.imgSearch),
            listOf(binding.btnSettings, binding.imgSettings)

        )
        showDialog = object : ShowDialog {
            override fun showDialog(context: Context) {
                binding.dialogDownloading.root.animAppear(context, duration = 1000)
                binding.dialogDownloading.fondo.setOnClickListener {
                }
            }

            override fun dimissDialog() {
                runOnUiThread {
                    binding.dialogDownloading.root.animVanish(
                        context = binding.dialogDownloading.root.context,
                        duration = 1000
                    )
                    MenuFragment.getCallBack().invoke()
                }
            }

            override fun setMensajeDialog(msg: String) {
                binding.dialogDownloading.txtMensaje.text = msg
            }
        }

        stateDowloand = object : StateDowloand {
            override fun setState(isSucessDowloand: Boolean) {
                settingsViewModel.saveStateDowloand(activity = this@MenuActivity, isSucessDowloand)
            }

            override fun getStateDowloand() {
                val state = settingsViewModel.getStateDowloand(activity = this@MenuActivity)
                Log.w("TAGGGGGG", "$state   asasasasasasasasasas")
                if (!state) {
                    getShowDialogListener().showDialog(this@MenuActivity)
                    getShowDialogListener().setMensajeDialog(resources.getString(R.string.eliminandoData))
                    settingsViewModel.sincronizarData(this@MenuActivity)
                }
            }

        }
        activeSincronized = object : StateSincronized {
            override fun activeSincronized(isActiveSincronized: Boolean) {
                settingsViewModel.setService(
                    context  = this@MenuActivity,
                    activity = this@MenuActivity,
                    isActiveSynchronized = isActiveSincronized
                )
            }

            override fun getActiveSincronized(): Boolean {
                return settingsViewModel.getActiveSincronized(context = this@MenuActivity)
            }

        }
        stateDowloand.getStateDowloand()

        binding.btnHome.setOnClickListener { view ->
            buttonsToolbar.forEach { views ->
                if (views == buttonsToolbar.get(0)) {
                    views.pressedButtonsToolbar(this, isPressed = true)
                } else {
                    views.pressedButtonsToolbar(this, isPressed = false)
                }
            }

            binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            invisibleFragments()

            MenuFragment.getCallBack().invoke()

            binding.fragmentContainerMenu.animAppear(context = view.context, 1000)


        }
        binding.btnSaved.setOnClickListener { view ->
            buttonsToolbar.forEach { views ->
                if (views == buttonsToolbar.get(1)) {
                    views.pressedButtonsToolbar(this, isPressed = true)
                } else {
                    views.pressedButtonsToolbar(this, isPressed = false)
                }
            }
            binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            SavedFragment.getDataCallBack().invoke()
            invisibleFragments()
            binding.fragmentContainerSaved.animAppear(context = view.context, 1000)
        }
        binding.btnSearch.setOnClickListener { view ->
            buttonsToolbar.forEach { views ->
                if (views == buttonsToolbar.get(2)) {
                    views.pressedButtonsToolbar(this, isPressed = true)
                } else {
                    views.pressedButtonsToolbar(this, isPressed = false)
                }
            }
            invisibleFragments()
            binding.fragmentContainerSearch.animAppear(view.context, 1000)
        }
        binding.btnSettings.setOnClickListener { view ->
            buttonsToolbar.forEach { views ->
                if (views == buttonsToolbar.get(3)) {
                    views.pressedButtonsToolbar(this, isPressed = true)
                } else {
                    views.pressedButtonsToolbar(this, isPressed = false)
                }
            }
            invisibleFragments()
            SettingsFragment.getCallBack().getData()
            binding.fragmentContainerSettings?.animAppear(view.context, 1000)
        }

        onScroll = object : OnScroll {
            override fun scrollChangeToolbar(isUp: Boolean) {
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = isUp, duration = 200L)
            }

            override fun scrollChangeButtom(isUp: Boolean) {
                binding.btnShowBook.animTraslateToBottomOrUp(isUp, 200L)
            }

            override fun showToolbar(show: Boolean) {
                if (show)
                    binding.cardToolbar.visibility = View.VISIBLE
                else
                    binding.cardToolbar.visibility = View.INVISIBLE
            }

            override fun showButtonBook(show: Boolean) {
                if (show)
                    binding.btnShowBook.visibility = View.VISIBLE
                else
                    binding.btnShowBook.visibility = View.GONE
            }
        }


    }

    fun invisibleFragments() {
        binding.fragmentContainerMenu.visibility     = View.INVISIBLE
        binding.fragmentContainerSearch.visibility   = View.INVISIBLE
        binding.fragmentContainerSaved.visibility    = View.INVISIBLE
        binding.fragmentContainerSettings.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
        println(global?.fragment.toString())
        when (global?.fragment) {
            NameFragments.BOOKMENU -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_to_nav_book_menuFragment)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)

            }
            NameFragments.BOOKSAVED -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_to_nav_book_savedFragment2)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(50)
                    SavedFragment.getDataCallBack().invoke()
                }
            }
            NameFragments.BOOKSEARCH -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_to_nav_book_searchFragment)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            }
            NameFragments.FORMSBOOK -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_formsFragment_to_menuFragment)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            }
            NameFragments.FORMSTHEME -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_formsFragment3_to_bookFragment4)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            }
            NameFragments.FORMSCONTENT -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_formsFragment3_to_contentFragment4)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            }
            NameFragments.FORMSCONTENTASEARCH -> {
                val bundle = bundleOf("type" to "search")
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_formsFragment2_to_contentFragment2, bundle)
                binding.cardToolbar.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            }

            NameFragments.CONTENTSSEARCH -> {
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_contentFragment_to_searchFragment)
                binding.btnShowBook.animTraslateToBottomOrUp(isUp = true, duration = 200L)

            }
            NameFragments.CONTENTS -> {
                val bundle = bundleOf("fragment" to NameFragments.BOOKMENU.toString())
                Navigation.findNavController(global.view)
                    .navigate(R.id.action_contentFragment4_to_bookFragment4, bundle)
                binding.btnShowBook.animTraslateToBottomOrUp(isUp = true, duration = 200L)
            }
            else -> finishAffinity()
        }
    }
}

