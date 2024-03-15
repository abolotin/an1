package ru.netology.nmedia.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostDaoImpl
import ru.netology.nmedia.util.DbHelper

class AppDb private constructor(db: SQLiteDatabase) {
    val postDao: PostDao = PostDaoImpl(db)

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: AppDb(
                    buildDatabase(context, arrayOf(PostDaoImpl.ddls))
                ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, ddls: Array<String>) = DbHelper(
            context = context,
            version = 1,
            name = "app.db",
            ddls = ddls)
            .writableDatabase
    }
}