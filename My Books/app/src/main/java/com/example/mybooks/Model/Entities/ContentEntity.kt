package com.example.mybooks.Model.Entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Content",
    foreignKeys = arrayOf(
    ForeignKey(entity = ThemeEntity::class,
    parentColumns = arrayOf("idTheme"),
    childColumns = arrayOf("fk_idTheme"),
    onDelete = ForeignKey.CASCADE)
))
data class ContentEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "idContent")
    val idContent: Int,
    @NonNull
    @ColumnInfo(name = "subTitle")
    val subTitle: String,
    @NonNull
    @ColumnInfo(name = "fk_idTheme")
    val fk_idTheme: Int
)
