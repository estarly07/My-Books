package com.example.mybooks.Model.Entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Theme",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = arrayOf("id_book"),
            childColumns = arrayOf("fk_idBook"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class ThemeEntity(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "idTheme")
    var idTheme: Int,

    @ColumnInfo(name = "name")
    @NonNull
    var name: String,
    @ColumnInfo(name = "importance")
    @NonNull
    var importance: Int,

    @ColumnInfo(name = "fk_idBook")
    @NonNull
    var fk_idBook: Int
)
