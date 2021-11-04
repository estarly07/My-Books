package com.example.mybooks.Model.Entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = "id")
    var id: Int,
    @ColumnInfo(name = "name")
    @NonNull
    var name: String,
    @ColumnInfo(name = "pass")
    @NonNull
    var pass: String,
    @ColumnInfo(name="photo")
    @NonNull
    var photo: String

)
