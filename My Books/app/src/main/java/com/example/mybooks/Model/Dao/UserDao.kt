package com.example.mybooks.Model.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mybooks.Model.Entities.UserEntity
import com.example.mybooks.Models.User

@Dao
interface UserDao {
    @Insert
    suspend fun registerUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM User WHERE name=:name AND pass=:pass")
    suspend fun login(name: String, pass: String): User?

    @Query("SELECT COUNT(*) FROM Book WHERE fk_id_user=:idUser")
    suspend fun getCountBooks(idUser: Int):Int
    @Query("SELECT COUNT(*) FROM Book WHERE fk_id_user=:idUser AND saved=1")
    suspend fun getCountBooksSave(idUser: Int):Int

    @Query("SELECT COUNT(*) FROM Theme WHERE fk_idBook=:idBook")
    suspend fun getCountTheme(idBook: Int):Int
    @Query("SELECT COUNT(*) FROM Theme WHERE fk_idBook=:idBook and importance=1")
    suspend fun getCountThemeSave(idBook: Int):Int
    @Query("UPDATE user set photo=:photo ")
    suspend fun updatePhoto(photo:String)
    @Query("UPDATE User SET name=:name")
    suspend fun updateName(name:String)
}