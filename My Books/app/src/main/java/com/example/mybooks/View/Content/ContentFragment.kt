package com.example.mybooks.View.Content

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybooks.Global
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Models.Content
import com.example.mybooks.NameFragments
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterContent
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.View.forms.FormsFragment
import com.example.mybooks.ViewModel.ContentViewModel
import com.example.mybooks.databinding.FragmentContentBinding
import com.example.mybooks.showToast


class ContentFragment : Fragment() {
    lateinit var _binding: FragmentContentBinding
    val binding get()    = _binding
    val contentViewModel : ContentViewModel by viewModels()

    val global  = Global.getInstance()
    val adapter = AdapterContent()
    val scroll  = MenuActivity.getOnScroll()

    companion object {
        private lateinit var theme: ThemeEntity
        fun setTheme(theme: ThemeEntity) {
            this.theme = theme
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (theme.importance == 1) {
            binding.btnThemeQuitarGuardar.visibility = View.VISIBLE
            binding.btnThemeGuardar.visibility       = View.GONE
        }
        global?.view = view

        if (arguments?.getString("type") == "search")
            global?.fragment = NameFragments.CONTENTSSEARCH
        else
            global?.fragment = NameFragments.CONTENTS
        super.onViewCreated(view, savedInstanceState)
        ContentViewModel.initUsecase(view.context)

        scroll?.showButtonBook(show = false)

        binding.reciclerContent.layoutManager =
            LinearLayoutManager(
                view.context,
                LinearLayoutManager.VERTICAL,
                false
            )
        binding.reciclerContent.setHasFixedSize(true)
        binding.reciclerContent.isNestedScrollingEnabled = false

        adapter.setClic(object : AdapterContent.Clic {
            override fun clic(
                textEntity        : TextEntity,
                positionText      : Int,
                positionContenido : Int,
                view              : View
            ) {
                if (textEntity.type == "TEXT") {
                    showDialogText(
                        view.context,
                        text            = textEntity.content,
                        textEntity      = textEntity,
                        positionContent = positionContenido,
                        positionText    = positionText
                    )
                } else {
                    showDialogImage(
                        view.context,
                        img             = textEntity.content,
                        textEntity      = textEntity,
                        positionContent = positionContenido,
                        positionText    = positionText
                    )
                }
            }

            override fun clicSubtitle(content: Content, position: Int, view: View) {
                showDialogSubtitle(
                    view.context,
                    content  = content,
                    position = position
                )
            }
        })
        binding.reciclerContent.adapter = adapter
        binding.btnAddContent.setOnClickListener {
            FormsFragment.setThema(theme)
            if (arguments?.getString("type") != "search") {
                val bundle = bundleOf("type" to "contenido")
                Navigation.findNavController(view)
                    .navigate(R.id.action_contentFragment4_to_formsFragment3, bundle)
            } else {
                val bundle = bundleOf("type" to "contenidosearch")
                Navigation.findNavController(view)
                    .navigate(R.id.action_contentFragment2_to_formsFragment2, bundle)
            }
        }
        binding.txtNameThemeContent.text = theme.name
        contentViewModel.listContent.observe(viewLifecycleOwner, { list ->
            adapter.setList(list)
        })
        binding.btnThemeGuardar.setOnClickListener { view ->

            binding.btnThemeQuitarGuardar.playAnimation()
            binding.btnThemeQuitarGuardar.visibility = View.VISIBLE
            binding.btnThemeGuardar.visibility       = View.GONE
            contentViewModel.saveTheme(
                idTheme = theme.idTheme,
                saved   = true
            )
        }
        binding.btnThemeQuitarGuardar.setOnClickListener { view ->

            binding.btnThemeQuitarGuardar.visibility = View.GONE
            binding.btnThemeGuardar.visibility = View.VISIBLE
            contentViewModel.saveTheme(
                idTheme = theme.idTheme,
                saved   = false
            )
        }
        contentViewModel.getContentByID(theme.idTheme)
    }

    private fun showDialogSubtitle(context: Context, content: Content, position: Int) {
        binding.dialogWrite.root.animAppear(context, duration = 1000)
        binding.dialogWrite.edtContentWrite.setText(content.subTitle)
        binding.dialogWrite.edtContentWrite.requestFocus()

        binding.dialogWrite.btnCancelDialogWrite.setOnClickListener {

            binding.dialogWrite.root.animVanish(it.context, 400)
        }
        binding.dialogWrite.btnAceptDialogWrite.setOnClickListener {
            val txt = binding.dialogWrite.edtContentWrite.text.toString().trim()
            addEditSubtitle(
                context         = it.context,
                subtitle        = txt,
                content         = content,
                positionContent = position
            )
        }
    }

    private fun showDialogText(
        context         : Context,
        text            : String,
        textEntity      : TextEntity,
        positionContent : Int,
        positionText    : Int
    ) {
        binding.dialogWrite.root.animAppear(context, 1000)
        binding.dialogWrite.edtContentWrite.setText(text)
        binding.dialogWrite.edtContentWrite.requestFocus()

        binding.dialogWrite.btnCancelDialogWrite.setOnClickListener {

            binding.dialogWrite.root.animVanish(it.context, 400)
        }
        binding.dialogWrite.btnAceptDialogWrite.setOnClickListener {
            val txt = binding.dialogWrite.edtContentWrite.text.toString().trim()
            addEdittext(
                context         = it.context,
                content         = txt,
                textEntity      = textEntity,
                positionContent = positionContent,
                positionText    = positionText
            )
        }
    }

    private fun showDialogImage(
        context         : Context,
        img             : String,
        textEntity      : TextEntity,
        positionContent : Int,
        positionText    : Int
    ) {
        binding.dialogImage.root.animAppear(context, 1000)
        binding.dialogImage.edtNameBook.setText(img)
        binding.dialogImage.edtNameBook.requestFocus()
        binding.dialogImage.btnAcept.setOnClickListener {
            addImage(
                context         = it.context,
                image           = binding.dialogImage.edtNameBook.text.toString().trim(),
                textEntity      = textEntity,
                positionContent = positionContent,
                positionText    = positionText
            )


        }
        binding.dialogImage.btnCancel.setOnClickListener {
            binding.dialogImage.root.animVanish(
                context  = it.context,
                duration = 300
            )

        }
    }

    private fun addImage(
        context         : Context,
        image           : String,
        textEntity      : TextEntity,
        positionContent : Int,
        positionText    : Int
    ): Unit {
        if (image == "") {
            "Llena el campo".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
            return
        }

        adapter.setObject(
            content           = image,
            positionText      = positionText,
            positionContenido = positionContent
        )
        contentViewModel.updateData(data = image, textEntity = textEntity)

    }

    private fun addEdittext(
        context         : Context,
        content         : String,
        textEntity      : TextEntity,
        positionContent : Int,
        positionText    : Int
    ) {
        if (content == "") {
            "Ingresa algo".showToast(
                context  = context,
                duration = Toast.LENGTH_SHORT,
                resource = R.layout.toast_login
            )
            return
        }

        adapter.setObject(
            content           = content,
            positionText      = positionText,
            positionContenido = positionContent
        )
        binding.dialogImage.root.animVanish(
            context  = context,
            duration = 300
        )
        binding.dialogWrite.root.animVanish(context, 400)
        contentViewModel.updateData(data = content, textEntity = textEntity)

    }

    private fun addEditSubtitle(
        context         : Context,
        subtitle        : String,
        content         : Content,
        positionContent : Int,
    ) {
        if (subtitle == "") {
            "Ingresa algo".showToast(
                context  = context,
                duration = Toast.LENGTH_SHORT,
                resource = R.layout.toast_login
            )
            return
        }

        adapter.setSubtitle(
            subtitle          = subtitle,
            positionContenido = positionContent
        )
        binding.dialogImage.root.animVanish(
            context  = context,
            duration = 300
        )
        binding.dialogWrite.root.animVanish(context, 400)
      contentViewModel.updateSubtitle(
          subtitle = subtitle,
          content  = content
      )

    }
}