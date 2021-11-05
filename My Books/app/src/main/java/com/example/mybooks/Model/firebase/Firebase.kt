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
            map[ID_BOOK.getName()]          = book.id_book
            map[NAME_BOOK.getName()]        = book.name
            map[IMAGE_BOOK.getName()]       = book.image
            map[CREATION_DATE.getName()]    = book.creation_date
            map[SAVED.getName()]            = book.saved
            map[DESCRIPTION.getName()]      = book.description
            map[LAST_TIME_OPEN.getName()]   = book.lastTimeOpen
            map[FK_ID_USER.getName()]       = book.fk_id_user

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
            map[ID_THEME.getName()]          = theme.idTheme
            map[NAME_THEME.getName()]        = theme.name
            map[IMPORTANCE_THEME.getName()]  = theme.importance
            map[FK_ID_BOOK.getName()]        = theme.fk_idBook

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
            map[ID_CONTENT.getName()]       = content.idContent
            map[SUBTITLE.getName()]         = content.subTitle
            map[FK_ID_THEME.getName()]    = content.fk_idTheme

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
            map[ID_TEXT.getName()]          = text.id_text
            map[CONTENT.getName()]          = text.content
            map[TYPE_TEXT.getName()]        = text.type
            map[FK_ID_CONTENT.getName()]    = text.fk_id_content

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
                            id_book         = (map[ID_BOOK.getName()] as Long).toInt(),
                            name            = map[NAME_BOOK.getName()].toString(),
                            image           = map[IMAGE_BOOK.getName()].toString(),
                            creation_date   = map[CREATION_DATE.getName()].toString(),
                            saved           = map[SAVED.getName()] as Boolean,
                            description     = map[DESCRIPTION.getName()].toString(),
                            lastTimeOpen    = map[LAST_TIME_OPEN.getName()].toString(),
                            fk_id_user      = (map[FK_ID_USER.getName()] as Long).toInt()
                        )

                        listBooks.add(bookEntity)
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
                    val list = if (snapshot.get(CAMPS_THEMES.getName()) != null) snapshot.get(
                        CAMPS_THEMES.getName()
                    ) as List<*> else listOf<ThemeEntity>()
                    list.forEach { value ->
                        val map = value as HashMap<*, *>
                        val theme = ThemeEntity(
                            idTheme         = (map[ID_THEME.getName()] as Long).toInt(),
                            name            =  map[NAME_THEME.getName()].toString(),
                            importance      = (map[IMPORTANCE_THEME.getName()] as Long).toInt(),
                            fk_idBook       = (map[FK_ID_BOOK.getName()] as Long).toInt()

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
                            idContent   = (map[ID_CONTENT.getName()] as Long).toInt(),
                            subTitle    =  map[SUBTITLE.getName()].toString(),
                            fk_idTheme  = (map[FK_ID_THEME.getName()] as Long).toInt()
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
                            id_text         = (map[ID_TEXT.getName()] as Long).toInt(),
                            type            = map[CONTENT.getName()].toString(),
                            content         = map[TYPE_TEXT.getName()].toString(),
                            fk_id_content   = (map[FK_ID_CONTENT.getName()] as Long).toInt()
                        )
                        listText.add(text)

                    }
                }
                callback.insertTexts(listText)
            }


    }

}