package com.example.mybooks.Models

import com.example.mybooks.Model.Entities.TextEntity

class Content {
    var idContent: Int = 0
    var subTitle = ""
    var fk_idTheme: Int = 0
    lateinit var arrayText: List<TextEntity>

}