package com.example.mybooks.Model.Dao

import androidx.room.*
import com.example.mybooks.Model.Entities.BookEntity

@Dao
interface BookDao {
    @Insert
    suspend fun insertBook(book: BookEntity): Long

    @Query("SELECT * FROM Book WHERE fk_id_user=:idUser")
    suspend fun getAllBooks(idUser: Int): List<BookEntity>

    @Query("UPDATE book SET saved=:isSaved WHERE id_book=:idBook")
    suspend fun updateStateBook(isSaved: Int, idBook: Int)

    @Query("SELECT * FROM Book WHERE lastTimeOpen=:date")
    suspend fun getRecentsBooks(date: String): List<BookEntity>

    @Query("UPDATE BOOK SET lastTimeOpen=:date WHERE id_book=:idBook")
    /**ACTUALIZAR LA FECHA EN QUE SE ABRIO EL LIBRO*/
    suspend fun updateDateOpen(date: String, idBook: Int)

    @Query("SELECT * FROM BOOK WHERE SAVED = 1")
    suspend fun getAllBooksSaved(): List<BookEntity>

    @Query("SELECT * FROM Book WHERE NAME LIKE '' || :name || '%' and fk_id_user=:idUser")
    suspend fun searhBooks(idUser: Int, name: String): List<BookEntity>

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM Book ")
    suspend fun deletAll()
}