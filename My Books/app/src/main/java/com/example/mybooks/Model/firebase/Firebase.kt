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

class Firebase {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebase = FirebaseFirestore.getInstance()
    val preferences=SharedPreferences.getInstance()
    private val namesCollectionsFirestore = listOf(
        //COLLECTIONS
        "Books",
        "Contents",
        "Texts",
        "Themes",

        //CAMPOS
        "books",
        "contents",
        "texts",
        "themes",
    )


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
    fun saveBooks(context: Context,listBooks: List<BookEntity>) {

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
            namesCollectionsFirestore[4] to array
        )

        firebase.collection(namesCollectionsFirestore[0]).document(preferences.getToken(context = context))
            .set(books)


    }

    /**SALVAR LOS TEMAS EN FIREBASE*/
    fun saveThemes(context: Context,listThemes: List<ThemeEntity>) {
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
            namesCollectionsFirestore[7] to array
        )
        firebase.collection(namesCollectionsFirestore[3]).document(preferences.getToken(context = context))
            .set(themes)

    }

    /**SALVAR LOS CONTENIDOS EN FIREBASE*/
    fun saveContents(context: Context,listContents: List<ContentEntity>) {
        val array = mutableListOf<Any>()
        listContents.forEach { content ->
            val map = HashMap<String, Any>()
            map["idContent"] = content.idContent
            map["subTitle"] = content.subTitle
            map["fk_idTheme"] = content.fk_idTheme

            array.add(map)

        }
        val contents = mapOf(
            namesCollectionsFirestore[5] to array
        )
        firebase.collection(namesCollectionsFirestore[1]).document(preferences.getToken(context = context))
            .set(contents)
    }

    /**SALVAR LOS TEXTS EN FIREBASE*/
    fun saveText(context: Context,listText: List<TextEntity>) {
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
            namesCollectionsFirestore[6] to array
        )
        firebase.collection(namesCollectionsFirestore[2]).document(preferences.getToken(context = context))
            .set(texts)
    }

    fun getBooksFirebase(context: Context, callback: CallBack) {
        firebase.collection(namesCollectionsFirestore[0]).document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val list = if(document.get(namesCollectionsFirestore[4]) != null){
                        document.get(namesCollectionsFirestore[4]) as List<*>
                    }else{
                        listOf<BookEntity>()
                    }
                    var listBooks = mutableListOf<BookEntity>()
                    list.forEach { any ->
                        val map: HashMap<String, *> = any as HashMap<String, *>
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

    fun getThemesFirebase(context: Context,callback: CallBack) {
        firebase.collection(namesCollectionsFirestore[3]).document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { snapshot ->
                val listThemes = mutableListOf<ThemeEntity>()
                if (snapshot != null) {
                    println("LISTATHEMES ${snapshot.get(namesCollectionsFirestore[7])}")
                    val list = if (snapshot.get(namesCollectionsFirestore[7])!=null)snapshot.get(namesCollectionsFirestore[7]) as List<*> else listOf<ThemeEntity>()
                    list.forEach { value ->
                        val map = value as HashMap<String, *>
                        val theme = ThemeEntity(
                            idTheme = (map["idTheme"] as Long).toInt(),
                            name = map["name"].toString(),
                            importance = (map["importance"] as Long).toInt(),
                            fk_idBook = (map["fk_idBook"] as Long).toInt()

                        )
                        listThemes.add(theme)
                    }
                }
                callback.insertThemes(context,listThemes)
            }
    }

    fun getContentsFirebase(context: Context,callback: CallBack) {
        firebase.collection(namesCollectionsFirestore[1]).document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { document ->
                val listContents = mutableListOf<ContentEntity>()
                if (document != null) {
                    val list = if (document.get(namesCollectionsFirestore[5])!=null)document.get(namesCollectionsFirestore[5]) as List<*>else listOf<ContentEntity>()
                    list.forEach { value ->
                        val map = value as HashMap<String, *>
                        val content = ContentEntity(
                            idContent = (map["idContent"] as Long).toInt(),
                            subTitle = map["subTitle"].toString(),
                            fk_idTheme = (map["fk_idTheme"] as Long).toInt()
                        )
                        listContents.add(content)
                    }

                }
                callback.insertContents(context,listContents)

            }
    }

    fun getTextFirebase(context: Context,callback: CallBack) {
        firebase.collection(namesCollectionsFirestore[2]).document(preferences.getToken(context = context)).get()
            .addOnSuccessListener { document ->
                val listText = mutableListOf<TextEntity>()
                if (document != null) {
                    val list =if ( document.get(namesCollectionsFirestore[6])!=null) document.get(namesCollectionsFirestore[6]) as List<*> else listOf<TextEntity>()
                    for (value in list) {
                        val map = value as HashMap<String, *>
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