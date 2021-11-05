package com.example.mybooks.Model.firebase

enum class NamesFirestore(val camp: String) {
    //Nombres de las colecciones
    COLLECTION_BOOK("Books"),
    COLLECTION_CONTENTS("Contents"),
    COLLECTION_TEXTS("Texts"),
    COLLECTION_THEMES("Themes"),

    //Nombres de los campos
    CAMPS_BOOK("books"),
    CAMPS_CONTENTS("contents"),
    CAMPS_TEXTS("texts"),
    CAMPS_THEMES("themes"),

    /**
     *llaves de los mapas
     * */
    //BOOK
    ID_BOOK("id_book"),
    NAME_BOOK("name"),
    IMAGE_BOOK("image"),
    CREATION_DATE("creation_date"),
    SAVED("saved"),
    DESCRIPTION("description"),
    LAST_TIME_OPEN("lastTimeOpen"),
    FK_ID_USER("fk_id_user"),

    //THEME
    ID_THEME("idTheme"),
    NAME_THEME("name"),
    IMPORTANCE_THEME("importance"),
    FK_ID_BOOK("fk_idBook"),

    //CONTENT
    ID_CONTENT("idContent"),
    SUBTITLE("subTitle"),
    FK_ID_THEME("fk_idTheme"),

    ID_TEXT("id_text"),
    CONTENT("content"),
    TYPE_TEXT("type"),
    FK_ID_CONTENT("fk_id_content"),
    ;

    fun getName(): String {
        return this.camp
    }
}