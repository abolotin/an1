package ru.netology.nmedia.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(
    context: Context?,
    name: String?,
    version: Int,
    private val ddls: Array<String>
) : SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.let { db ->
            ddls.forEach { query ->
                db.execSQL(query)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}