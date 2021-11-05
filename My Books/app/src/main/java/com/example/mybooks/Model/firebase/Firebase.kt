package com.example.mybooks.Model.firebase

import android.content.Context
import com.example.mybooks.Model.Entities.BookEntity
import com.example.mybooks.Model.Entities.ContentEntity
import com.example.mybooks.Model.Entities.TextEntity
import com.example.mybooks.Model.Entities.ThemeEntity
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.View.settings.SettingsFragment
import com.example.mybooks.ViewModel.SettingsViewModel.CallBack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.HashMap
import com.example.mybooks.Model.firebase.NamesFirestore.*


class Firebase {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebase = FirebaseFirestore.getInstance()
    private val preferences = SharedPreferences.getInstance()

    companion object {
        private var INSTANCE: Firebase? = null
        fun getInstance(): Firebase {
            if (INSTANCE == null)
                INSTANCE = Firebase()

            return INSTANCE!!
        }
    }


    fun registerUser(email: String, pass: String, lister: SettingsFragment.RegisterEmail) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            println(auth.currentUser?.uid)
            if (task.isSuccessful) {
                lister.alert("Se registro tu correo")
                lister.dimissDialog(auth.currentUser!!.uid)
            } else {
                lister.alert("Ese correo ya existe")

            }
        }
    }

    fun loginUserEmail(email: String, pass: String, lister: SettingsFragment.RegisterEmail) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                lister.alert("Inicio de sesión realizado")
                lister.dimissDialog(auth.currentUser!!.uid)
            } else {
                lister.alert("Esos datos no existe")

            }
        }
    }

    /**SALVAR LOS LIBROS EN FIREBASE*/
    fun saveBooks(context: Context, listBooks: List<BookEntity>) {

        val array = mutableListOf<Any>()
        listBooks.forEach { book ->
            val map = HashMap<String, Any>()
            map["id_book"] = book.id_book
            map["name"] = book.name
            map["image"] = book.image
            map["creation_date"] = book.creation_date
            map["saved"] = book.saved
            map["description"] = book.description
            map["lastTimeOpen"] = book.lastTimeOpen
            map["fk_id_user"] = book.fk_id_user

            array.add(map)

        }

        val books = mapOf(
            CAMPS_BOOK.getName() to array
        )

        firebase.collection(COLLECTION_BOOK.getName())
            .document(preferences.getToken(context = context))
            .set(books)


    }

    /**SALVAR LOS TEMAS EN FIREBASE*/
    fun saveThemes(context: Context, listThemes: List<ThemeEntity>) {
        val array = mutableListOf<Any>()
        listThemes.forEach { theme ->
            val map = HashMap<String, Any>()
            map["idTheme"] = theme.idTheme
            map["name"] = theme.name
            map["importance"] = theme.importance
            map["fk_idBook"] = theme.fk_idBook

            array.add(map)

        }
        val themes = mapOf(
           CAMPS_THEMES.getName() to array
        )
        firebase.collection(COLLECTION_THEMES.getName())
            .document(preferences.getToken(context = context))
            .set(themes)

    }

    /**SALVAR LOS CONTENIDOS EN FIREBASE*/
    fun saveContents(context: Context, listContents: List<ContentEntity>) {
        val array = mutableListOf<Any>()
        listContents.forEach { content ->
            val map = HashMap<String, Any>()
            map["idContent"] = content.idContent
            map["subTitle"] = content.subTitle
            map["fk_idTheme"] = content.fk_idTheme

            array.add(map)

        }
        val contents = mapOf(
            CAMPS_CONTENTS.getName() to array
        )
        firebase.collection(COLLECTION_CONTENTS.getName())
            .document(preferences.getToken(context = context))
            .set(contents)
    }

    /**SALVAR LOS TEXTS EN FIREBASE*/
    fun saveText(context: Context, listText: List<TextEntity>) {
        val array = mutableListOf<Any>()
        listText.forEach { text ->
            val map = HashMap<String, Any>()
            map["id_text"] = text.id_text
            map["content"] = text.content
            map["type"] = text.type
            map["fk_id_content"] = text.fk_id_content

            array.add(map)

        }
        val texts = mapOf(
            CAMPS_TEXTS.getName() to array
        )
        firebase.collection(COLLECTION_TEXTS.getName())
            .document(preferences.getToken(context = context))
            .set(texts)
    }

    fun getBooksFirebase(context: Context, callback: CallBack) {
        firebase.collection(COLLECTION_BOOK.getName())
            .document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val list = if (document.get(CAMPS_BOOK.getName()) != null) {
                        document.get(CAMPS_BOOK.getName()) as List<*>
                    } else {
                        listOf<BookEntity>()
                    }
                    val listBooks = mutableListOf<BookEntity>()
                    list.forEach { any ->
                        val map: HashMap<*, *> = any as HashMap<*, *>
                        println(map)
                        val bookEntity = BookEntity(
                            id_book = (map["id_book"] as Long).toInt(),
                            name = map["name"].toString(),
                            image = map["image"].toString(),
                            creation_date = map["creation_date"].toString(),
                            saved = map["saved"] as Boolean,
                            description = map["description"].toString(),
                            lastTimeOpen = map["lastTimeOpen"].toString(),
                            fk_id_user = (map["fk_id_user"] as Long).toInt()
                        )

                        listBooks.add(bookEntity)
                        println("1 ${map["image"]}")
                    }
                    callback.insertBooks(context = context, listBooks)

                }
            }
    }

    fun getThemesFirebase(context: Context, callback: CallBack) {
        firebase.collection(COLLECTION_THEMES.getName())
            .document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { snapshot ->
                val listThemes = mutableListOf<ThemeEntity>()
                if (snapshot != null) {
                    println("LISTATHEMES ${snapshot.get(CAMPS_THEMES.getName())}")
                    val list = if (snapshot.get(CAMPS_THEMES.getName()) != null) snapshot.get(
                        CAMPS_THEMES.getName()
                    ) as List<*> else listOf<ThemeEntity>()
                    list.forEach { value ->
                        val map = value as HashMap<*, *>
                        val theme = ThemeEntity(
                            idTheme = (map["idTheme"] as Long).toInt(),
                            name = map["name"].toString(),
                            importance = (map["importance"] as Long).toInt(),
                            fk_idBook = (map["fk_idBook"] as Long).toInt()

                        )
                        listThemes.add(theme)
                    }
                }
                callback.insertThemes(context, listThemes)
            }
    }

    fun getContentsFirebase(context: Context, callback: CallBack) {
        firebase.collection(COLLECTION_CONTENTS.getName())
            .document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { document ->
                val listContents = mutableListOf<ContentEntity>()
                if (document != null) {
                    val list = if (document.get(CAMPS_CONTENTS.getName()) != null) document.get(
                        CAMPS_CONTENTS.getName()
                    ) as List<*> else listOf<ContentEntity>()
                    list.forEach { value ->
                        val map = value as HashMap<*, *>
                        val content = ContentEntity(
                            idContent = (map["idContent"] as Long).toInt(),
                            subTitle = map["subTitle"].toString(),
                            fk_idTheme = (map["fk_idTheme"] as Long).toInt()
                        )
                        listContents.add(content)
                    }

                }
                callback.insertContents(context, listContents)

            }
    }

    fun getTextFirebase(context: Context, callback: CallBack) {
        firebase.collection(COLLECTION_TEXTS.getName())
            .document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { document ->
                val listText = mutableListOf<TextEntity>()
                if (document != null) {
                    val list = if (document.get(CAMPS_TEXTS.getName()) != null) document.get(
                        CAMPS_TEXTS.getName()
                    ) as List<*> else listOf<TextEntity>()
                    for (value in list) {
                        val map = value as HashMap<*, *>
                        val text = TextEntity(
                            id_text = (map["id_text"] as Long).toInt(),
                            type = map["type"].toString(),
                            content = map["content"].toString(),
                            fk_id_content = (map["fk_id_content"] as Long).toInt()
                        )
                        listText.add(text)

                    }
                }
                callback.insertTexts(listText)
            }


    }

}