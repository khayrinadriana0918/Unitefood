package com.example.groupproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UniteFood.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(
        username: String,
        email: String,
        password: String
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }

        return try {
            db.insertOrThrow(TABLE_USERS, null, values)
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun loginUser(
        email: String,
        password: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT $COLUMN_ID
            FROM $TABLE_USERS
            WHERE $COLUMN_EMAIL = ?
            AND $COLUMN_PASSWORD = ?
            """.trimIndent(),
            arrayOf(email, password)
        )

        val loggedIn = cursor.count > 0

        cursor.close()
        db.close()

        return loggedIn
    }
}