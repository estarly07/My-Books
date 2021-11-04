package com.example.mybooks.Model.Entities

import androidx.room.*


@Entity(foreignKeys = arrayOf(ForeignKey(entity = ContentEntity::class,
    parentColumns = arrayOf("idContent"),
    childColumns = arrayOf("fk_id_content"),
    onDelete = ForeignKey.CASCADE)))
data class TextEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_text")
    var id_text: Int,
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "fk_id_content")
    var fk_id_content: Int
)
