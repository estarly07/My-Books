package com.example.mybooks.Model.Dao

import androidx.room.*
import com.example.mybooks.Model.Entities.ThemeEntity

@Dao
interface ThemeDao {
    @Insert
    fun insertTheme(theme: ThemeEntity): Long

    @Update
    suspend fun updateTheme(theme: ThemeEntity)

    @Query("SELECT * FROM Theme WHERE fk_idBook=:fk_idBook")
    fun getThemes(fk_idBook: Int): List<ThemeEntity>

    @Query("DELETE FROM Theme WHERE idTheme=:idTheme")
    fun deletTheme(idTheme: Int)

    @Query("SELECT * FROM Theme WHERE importance=1 AND fk_idBook=:fk_idBook")
    fun getSavedThemes(fk_idBook: Int): List<ThemeEntity>

    @Query("SELECT * FROM Theme WHERE fk_idBook=:idBook and name LIKE '' ||:name|| '%'")
    suspend fun searchThemes(idBook: Int, name: String): List<ThemeEntity>

    @Query("UPDATE theme SET importance =:saved WHERE idTheme=:idTheme")
    suspend fun savedTheme(idTheme: Int, saved: Boolean)

}