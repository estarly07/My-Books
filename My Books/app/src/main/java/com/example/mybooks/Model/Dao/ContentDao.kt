package com.example.mybooks.Model.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mybooks.Model.Entities.ContentEntity

@Dao
interface ContentDao {
    @Query("SELECT * FROM CONTENT WHERE fk_idTheme=:idTheme")
    fun getContentById(idTheme: Int): List<ContentEntity>

    @Insert
    fun insertContent(contentEntity: ContentEntity):Long

    @Update
    fun updateContent(contentEntity: ContentEntity)
}