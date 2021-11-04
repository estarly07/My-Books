package com.example.mybooks.Model.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mybooks.Model.Entities.TextEntity

@Dao
interface TextDao {
    @Insert
    suspend fun insertData(data: TextEntity)

    @Query("SELECT * FROM textentity WHERE fk_id_content=:idContent")
    suspend fun getData(idContent: Int): List<TextEntity>

    @Update
    suspend fun updateData(data: TextEntity)

}