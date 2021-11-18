package com.example.mybooks.Models

class User {
             var id    : Int    =0
    lateinit var name  : String
    lateinit var pass  : String
    lateinit var photo : String

    companion object {
        private var INSTANCE: User? = null
         fun getInstance(): User {
            if (INSTANCE == null)
                INSTANCE = User()
            return INSTANCE as User
        }
    }

}

