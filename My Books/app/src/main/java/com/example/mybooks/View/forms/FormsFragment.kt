package com.example.mybooks.View.forms

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybooks.Global
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.NameFragments
import com.example.mybooks.R
import com.example.mybooks.View.Adapters.AdapterContentText
import com.example.mybooks.View.Animations.animAppear
import com.example.mybooks.View.Animations.animVanish
import com.example.mybooks.View.Menu.MenuActivity
import com.example.mybooks.ViewModel.FormsViewModel
import com.example.mybooks.databinding.FragmentFormsBinding
import com.example.mybooks.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormsFragment : Fragment() {
    lateinit var _binding : FragmentFormsBinding
    val binding get()     = _binding
    val global            = Global.getInstance()
    val formsViewModel    : FormsViewModel by viewModels()
    val adapter           = AdapterContentText()
    var cont              = 0

    companion object {
        private var book: BookEntity? = null
        fun setBook(book: BookEntity) {
            this.book = book
        }

        private lateinit var theme: ThemeEntity
        fun setThema(theme: ThemeEntity) {
            this.theme = theme
        }

        private var deleteEvent: (()->Unit)? = null
        fun listenDelectEvent(deleteEvent: ()->Unit) {
            this.deleteEvent = deleteEvent
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFormsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    var formType: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formType = arguments?.getString("type").toString()

        //TIPO DE FORMULARIO A MOSTRAR
        when (formType) {
            "book" -> {
                binding.formBook.root.visibility = View.VISIBLE
                global?.fragment = NameFragments.FORMSBOOK
            }
            "bookedit" -> {
                binding.formBook.root.visibility = View.VISIBLE
                binding.formBook.edtUrlBook.setText(book?.image)
                binding.formBook.edtDescriptionBook.setText(book?.description)
                binding.formBook.edtNameBook.setText(book?.name)
                binding.formBook.btnAddBook.setText("Modificar libro")
                global?.fragment = NameFragments.FORMSBOOK
            }
            "tema" -> {
                binding.formTheme.root.visibility = View.VISIBLE
                global?.fragment = NameFragments.FORMSTHEME
            }
            "temaedit" -> {
                binding.formTheme.root.visibility = View.VISIBLE
                binding.formTheme.edtNameBook.setText(theme.name)
                binding.formTheme.btnAddBook.setText("Modificar tema")
                global?.fragment = NameFragments.FORMSTHEME
            }
            "contenido" -> {
                binding.contenedorContents.visibility = View.GONE
                binding.formContent.root.visibility   = View.VISIBLE
                global?.fragment = NameFragments.FORMSCONTENT
                adapter.setClic(object : AdapterContentText.Clic {
                    override fun clic(textEntity: TextEntity, position: Int, view: View) {
                        if (textEntity.type == "TEXT") {
                            showDialogText(
                                context  = view.context,
                                text     = textEntity.content,
                                isModify = true,
                                position = position
                            )
                        } else {
                            showDialogImage(
                                context  = view.context,
                                img      = textEntity.content,
                                isModify = true,
                                position = position
                            )
                        }

                    }

                })

            }
            "contenidosearch" -> {
                binding.contenedorContents.visibility = View.GONE
                binding.formContent.root.visibility   = View.VISIBLE
                global?.fragment = NameFragments.FORMSCONTENTASEARCH
                adapter.setClic(object : AdapterContentText.Clic {
                    override fun clic(textEntity: TextEntity, position: Int, view: View) {
                        if (textEntity.type == "TEXT") {
                            showDialogText(
                                context  = view.context,
                                text     = textEntity.content,
                                isModify = true,
                                position = position
                            )
                        } else {
                            showDialogImage(
                                context  = view.context,
                                img      = textEntity.content,
                                isModify = true,
                                position = position
                            )
                        }

                    }

                })

            }
        }

        global?.view = view
        MenuActivity.getOnScroll()?.showButtonBook(show = false)
        MenuActivity.getOnScroll()?.showToolbar   (show = false)

        binding.formBook.btnAddBook.setOnClickListener {
            if (binding.formBook.btnAddBook.text == "Modificar libro")
                formsViewModel.updateBook(
                    context = it.context,
                    listOf(
                        binding.formBook.edtNameBook.text.trim().toString(),
                        binding.formBook.edtUrlBook.text.trim().toString(),
                        binding.formBook.edtDescriptionBook.text.trim().toString()
                    ),
                    book!!
                )
            else
                formsViewModel.insertBook(
                    view.context,
                    listOf(
                        binding.formBook.edtNameBook.text.trim().toString(),
                        binding.formBook.edtUrlBook.text.trim().toString(),
                        binding.formBook.edtDescriptionBook.text.trim().toString()
                    )

                )
            binding.formBook.edtNameBook.setText("")
            binding.formBook.edtUrlBook.setText("")
            binding.formBook.edtDescriptionBook.setText("")
        }
        binding.formTheme.btnAddBook.setOnClickListener {
            if (binding.formTheme.btnAddBook.text == "Modificar tema") {
                formsViewModel.updateTheme(
                    context  = view.context,
                    data     = listOf(
                        binding.formTheme.edtNameBook.text.trim().toString(),
                    ),
                    themeOld = theme
                )
                binding.formTheme.edtNameBook.setText("")
            } else {
                formsViewModel.insertTheme(
                    context = view.context,
                    data    = listOf(
                        "0",
                        binding.formTheme.edtNameBook.text.trim().toString(),
                        "false",
                        book?.id_book.toString()
                    )
                )
                binding.formTheme.edtNameBook.setText("")
            }

        }
        binding.formContent.reciclerContentText.setHasFixedSize(true)
        binding.formContent.reciclerContentText.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        binding.formContent.reciclerContentText.adapter = adapter

        binding.formContent.btnImageContent.setOnClickListener {
            showDialogImage(
                context  = it.context,
                img      = "",
                isModify = false,
                position = 0)

        }
        binding.formContent.btnWriteContent.setOnClickListener {
            showDialogText(
                it.context,
                text     = "",
                isModify = false,
                position = 0)

        }
        binding.formContent.btnAddText.setOnClickListener { view ->

            formsViewModel.insertContent(
                context  = view.context,
                subTitle = binding.formContent.edtSubtitle.text.toString().trim(),
                idTheme  = theme.idTheme,
                data     = this.adapter.getList(),
                view     = view,
                type     = formType
            )


        }
        binding.formContent.btnDeleteContent.setOnClickListener {
            deleteEvent?.invoke()
            if (adapter.getList().isEmpty())
                binding.formContent.containerEmpty.visibility = View.VISIBLE
        }


    }

    private fun addImage(context: Context, image: String, isModify: Boolean, position: Int): Unit {
        if (image == "") {
            "Llena el campo".showToast(context, Toast.LENGTH_SHORT, R.layout.toast_login)
            return
        }
        if (isModify) {
            adapter.setObject(
                content  = image,
                position = position)
        } else {
            binding.formContent.containerEmpty.visibility = View.GONE
            cont++
            adapter.setList(
                TextEntity(
                    id_text       = cont,
                    content       = image,
                    type          = "IMAGE",
                    fk_id_content = 0
                )
            )
            binding.formContent.dialogImage.root.animVanish(
                context  = context,
                duration = 300
            )
        }
    }

    private fun addEdittext(context: Context, content: String, isModify: Boolean, position: Int) {
        if (content == "") {
            "Ingresa algo".showToast(
                context  = context,
                duration = Toast.LENGTH_SHORT,
                resource = R.layout.toast_login
            )
            return
        }
        if (isModify) {
            adapter.setObject(
                content  = content,
                position = position
            )
        } else {
            binding.formContent.containerEmpty.visibility = View.GONE
            cont++
            adapter.setList(
                TextEntity(
                    id_text       = cont,
                    content       = content,
                    type          = "TEXT",
                    fk_id_content = 0
                )
            )
        }
    }

    private fun showDialogText(context: Context, text: String, isModify: Boolean, position: Int) {
        binding.formContent.dialogWrite.root.animAppear(context, 1000)
        binding.formContent.dialogWrite.edtContentWrite.setText(text)
        binding.formContent.dialogWrite.edtContentWrite.requestFocus()

        binding.formContent.dialogWrite.btnCancelDialogWrite.setOnClickListener {
            binding.formContent.dialogWrite.root.animVanish(it.context, 400)
        }
        binding.formContent.dialogWrite.btnAceptDialogWrite.setOnClickListener {
            val txt = binding.formContent.dialogWrite.edtContentWrite.text.toString().trim()
            addEdittext(
                context  = it.context,
                content  = txt,
                isModify = isModify,
                position = position
            )
            binding.formContent.dialogWrite.root.animVanish(it.context, 200)
        }
    }

    private fun showDialogImage(context: Context, img: String, isModify: Boolean, position: Int) {
        binding.formContent.dialogImage.root.animAppear(context, 1000)
        binding.formContent.dialogImage.edtNameBook.setText(img)
        binding.formContent.dialogImage.edtNameBook.requestFocus()
        binding.formContent.dialogImage.btnAcept.setOnClickListener {
            addImage(
                context  = it.context,
                image    = binding.formContent.dialogImage.edtNameBook.text.toString().trim(),
                isModify = isModify,
                position = position
            )
            binding.formContent.dialogImage.root.animVanish(
                context  = it.context,
                duration = 300
            )

        }
        binding.formContent.dialogImage.btnCancel.setOnClickListener {
            binding.formContent.dialogImage.root.animVanish(
                context  = it.context,
                duration = 200
            )

        }
    }

}