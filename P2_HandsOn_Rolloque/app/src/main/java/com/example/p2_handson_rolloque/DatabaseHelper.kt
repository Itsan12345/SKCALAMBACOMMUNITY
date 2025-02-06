package com.example.p2_handson_rolloque

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "user_database"
        const val DATABASE_VERSION = 1
        const val TABLE_USER = "user"
        const val COLUMN_ID = "id"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_MIDDLE_NAME = "middle_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_AGE = "age"
        const val COLUMN_DOB = "dob"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_USER (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_FIRST_NAME TEXT, " +
                "$COLUMN_LAST_NAME TEXT, " +
                "$COLUMN_MIDDLE_NAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_AGE TEXT, " +
                "$COLUMN_DOB TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun updateUser(contentValues: ContentValues): Int {
        val db = writableDatabase
        return db.update(TABLE_USER, contentValues, "$COLUMN_ID = ?", arrayOf("1"))  // Update user with ID 1, replace with actual ID logic
    }
}
