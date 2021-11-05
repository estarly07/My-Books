package com.example.mybooks.Model.firebase

enum class NamesFirestore (val camp: String) {
    //Nombres de las colecciones
    COLLECTION_BOOK("Books"),
    COLLECTION_CONTENTS("Contents"),
    COLLECTION_TEXTS("Texts"),
    COLLECTION_THEMES("Themes"),

    //Nombres de los campos
    CAMPS_BOOK("books"),
    CAMPS_CONTENTS("contents"),
    CAMPS_TEXTS("texts"),
    CAMPS_THEMES("themes");

    fun getName(): String {
        return this.camp
    }
}