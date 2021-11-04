package com.example.mybooks.Model.Entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id_book")
    var id_book: Int,
    @NonNull
    @ColumnInfo(name = "name")
    var name: String,
    @NonNull
    @ColumnInfo(name = "image")
    var image: String,
    @NonNull
    @ColumnInfo(name = "creation_date")
    var creation_date: String,
    @NonNull
    @ColumnInfo(name = "saved")
    var saved: Boolean,
    @NonNull
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name="lastTimeOpen")
    var lastTimeOpen:String,
    @NonNull
    @ColumnInfo(name = "fk_id_user")
    var fk_id_user: Int
)
