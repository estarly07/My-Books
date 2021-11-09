package com.example.mybooks.Model.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mybooks.Model.Dao.*
import com.example.mybooks.Model.Entities.*

@Database(
    entities = [
        UserEntity    ::class,
        BookEntity    ::class,
        ThemeEntity   ::class,
        ContentEntity ::class,
        TextEntity    ::class
               ],
    version = 1
)
abstract class SqliteDb : RoomDatabase() {
    abstract fun userDao   () : UserDao

    /**LOS PERMISOS QUE TIENE (LISTAR,ESCRIBIR,ACTUALIZAR O ELIMINAR)*/
    abstract fun BookDao   () : BookDao
    abstract fun ThemeDao  () : ThemeDao
    abstract fun contentDao() : ContentDao
    abstract fun textDao   () : TextDao

    companion object {
        private var INSTANCE  : SqliteDb? = null

        fun getInstance(context: Context): SqliteDb? {
            if (INSTANCE != null) {
                return INSTANCE
            }
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    SqliteDb::class.java,
                    "Book"
                ).allowMainThreadQueries()
                    .build()
                return INSTANCE
            }
        }
    }
}