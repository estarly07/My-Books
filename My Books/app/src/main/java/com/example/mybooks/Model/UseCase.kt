package com.example.mybooks.Model

import android.app.Activity
import android.content.Context
import com.example.mybooks.Model.Dao.*
import com.example.mybooks.Model.Database.SqliteDb
import com.example.mybooks.Model.Entities.*
import com.example.mybooks.Model.SharedPreferences.SharedPreferences
import com.example.mybooks.Model.firebase.Firebase
import com.example.mybooks.Models.User
import com.example.mybooks.View.settings.SettingsFragment
import com.example.mybooks.ViewModel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.CountDownLatch

class UseCase(context: Context) {

    private var userDao: UserDao = SqliteDb.getInstance(context)?.userDao()!!
    private var bookDao: BookDao = SqliteDb.getInstance(context)?.BookDao()!!
    private var themeDao: ThemeDao = SqliteDb.getInstance(context)?.ThemeDao()!!
    private var contentDao: ContentDao = SqliteDb.getInstance(context)?.contentDao()!!
    private var textDao: TextDao = SqliteDb.getInstance(context)?.textDao()!!
    private var firebase = Firebase.getInstance()
    private var prefereces = SharedPreferences.getInstance()


    suspend fun createUser(user: UserEntity): Boolean {
        return userDao.registerUser(user) >= 1L
    }

    /**FUNCION PARA LOGUEARSE
     *@param data Es un array de parametros que contendra name,pass
     */
    suspend fun login(data: List<String>): User? {
        return userDao.login(data[0], data[1])
    }

    suspend fun insertBook(book: BookEntity): Long {
        return bookDao.insertBook(book)
    }

    suspend fun getAllBooks(idUser: Int): List<BookEntity> {
        return bookDao.getAllBooks(idUser)
    }

    suspend fun getAllBooksSaved(): List<BookEntity> {
        return bookDao.getAllBooksSaved()
    }

    suspend fun insertTheme(theme: ThemeEntity): Long {
        return themeDao.insertTheme(theme)
    }

    suspend fun getThemes(fk_idBook: Int): List<ThemeEntity> {
        return themeDao.getThemes(fk_idBook)
    }

    suspend fun deleteTheme(idTheme: Int) {
        themeDao.deletTheme(idTheme)
    }

    suspend fun getSavedThemes(fk_idBook: Int): List<ThemeEntity> {
        return themeDao.getSavedThemes(fk_idBook)
    }

    suspend fun updateStateBook(isSaved: Int, idBook: Int) {
        bookDao.updateStateBook(isSaved, idBook)
    }

    suspend fun getRecentsBooks(date: String): List<BookEntity> {
        return bookDao.getRecentsBooks(date)
    }

    suspend fun updateDateOpen(date: String, idBook: Int) {
        bookDao.updateDateOpen(date = date, idBook = idBook)
    }

    suspend fun searchBooks(idUser: Int, name: String): List<BookEntity> {
        return bookDao.searhBooks(idUser = idUser, name = name)
    }

    suspend fun searchThemes(idBook: Int, name: String): List<ThemeEntity> {
        return themeDao.searchThemes(idBook = idBook, name = name)
    }

    suspend fun getContentById(idTheme: Int): List<ContentEntity> {
        return contentDao.getContentById(idTheme)
    }

    suspend fun insertContent(contentEntity: ContentEntity): Long {
        return contentDao.insertContent(contentEntity)
    }

    suspend fun getDataContent(idContent: Int): List<TextEntity> {
        return textDao.getData(idContent)
    }

    suspend fun insertTextData(data: TextEntity) {
        textDao.insertData(data)
    }

    suspend fun updateData(data: TextEntity) {
        textDao.updateData(data)
    }

    suspend fun updateBook(data: BookEntity) {
        bookDao.updateBook(data)
    }

    suspend fun updateTheme(theme: ThemeEntity) {
        themeDao.updateTheme(theme)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun savedTheme(idTheme: Int, save: Boolean) {
        themeDao.savedTheme(idTheme, save)
    }

    suspend fun updateContent(content: ContentEntity) {
        contentDao.updateContent(content)
    }

    suspend fun getCountBook(idUser: Int): Int {
        return userDao.getCountBooks(idUser)
    }

    suspend fun getCountTheme(idBook: Int): Int {
        return userDao.getCountTheme(idBook)
    }

    suspend fun getCountBookSave(idUser: Int): Int {
        return userDao.getCountBooksSave(idUser)
    }

    suspend fun getCountThemeSave(idBook: Int): Int {
        return userDao.getCountThemeSave(idBook)
    }

    suspend fun updatePhoto(photo: String) {
        userDao.updatePhoto(photo)
    }

    suspend fun updateName(name: String) {
        userDao.updateName(name)
    }

    fun registerUserFirebase(email: String, pass: String, lister: SettingsFragment.RegisterEmail) {
        firebase.registerUser(email = email, pass = pass, lister = lister)
    }
    fun loginUserFirebase(email: String, pass: String, lister: SettingsFragment.RegisterEmail) {
        firebase.loginUserEmail(email = email, pass = pass, lister = lister)
    }

    fun saveBooksFirebase(context: Context,list: List<BookEntity>) {
        firebase.saveBooks(context,list)
    }

    fun saveThemesFirebase(context: Context,list: List<ThemeEntity>) {
        firebase.saveThemes(context,list)
    }

    fun saveContentFirebase(context: Context,list: List<ContentEntity>) {
        firebase.saveContents(context,list)
    }

    fun saveTextFirebase(context: Context,list: List<TextEntity>) {
        firebase.saveText(context,list)
    }

    fun getBooksFirebase(context: Context, callBack: SettingsViewModel.CallBack) {
        firebase.getBooksFirebase(context, callBack)
    }

    fun getThemesFirebase(context: Context,callBack: SettingsViewModel.CallBack) {
        firebase.getThemesFirebase(context,callBack)
    }

    fun getContentFirebase(context: Context,callBack: SettingsViewModel.CallBack) {
        firebase.getContentsFirebase(context,callBack)
    }

    fun getTextFirebase(context: Context,callBack: SettingsViewModel.CallBack) {
        firebase.getTextFirebase(context,callBack)
    }

    /**ELIMINAR TODO DE SQLITE*/
    suspend fun deleteAll() {
        bookDao.deletAll()
    }

    fun saveStateDowloand(activity: Activity, isSucessDowloand: Boolean) {
        prefereces.saveStateDescarga(activity, isSucessDowloand = isSucessDowloand)
    }

    fun getStateDowloand(activity: Activity): Boolean {
        return prefereces.getStateDowloand(activity)
    }

    fun activeSincronized(activity: Activity, isActiveSincronized: Boolean) {
        prefereces.activeSincronized(activity = activity, isActiveSincronized = isActiveSincronized)
    }

    fun getActiveSincronized(context: Context): Boolean {
        return prefereces.getActiveSincronized(context)
    }

    fun getCountSincronized(context: Context): Int {
        return prefereces.getCountSincronized(context)
    }
    fun getToken(context: Context):String{
        return prefereces.getToken(context)
    }
    fun saveToken(context: Context,token:String){
        prefereces.saveToken(context, token)
    }
}