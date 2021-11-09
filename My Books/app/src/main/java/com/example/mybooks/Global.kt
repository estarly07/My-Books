package com.example.mybooks

import android.view.View

class Global {
    var fragment = NameFragments.MENU
    lateinit var view: View

    companion object {
        private  var global: Global? =null
        fun getInstance(): Global? {
            if (global == null) {
                global = Global()
            }
            return global
        }
    }
}

/**SABER EN CUAL FRAGMENT ESTA*/
enum class NameFragments {
    MENU,
    BOOKMENU,               /**SU ANTECEDOR ES MENU*/
    BOOKSAVED,              /**SU ANTECEDOR ES SAVED*/
    BOOKSEARCH,             /**SU ANTECEDOR ES SEARCH*/
    FORMSBOOK,              /**SU ANTECEDOR ES MENU*/
    FORMSTHEME,             /**SU ANTECEDOR ES BOOK*/
    FORMSCONTENT,           /**SU ANTECEDOR ES CONTENT*/
    FORMSCONTENTASEARCH,    /**SU ANTECEDOR ES CONTENTSEARCH*/

    CONTENTSSEARCH,         /**SU ANTECEDOR ES BOOKSEARCH*/
    CONTENTS,



}