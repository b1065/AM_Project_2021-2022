package com.example.losulosu.DB.DB

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.example.losulosu.DB.Model.EmpModelClass

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Players"
        private const val TABLE_PLAYERS = "PlayersInfo"
        private const val KEY_LOGIN = "login"
        private const val KEY_PASSWORD = "password"
        private const val KEY_SCORE = "score"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_PLAYERS_TABLE = ("CREATE TABLE " + TABLE_PLAYERS + "("
                + KEY_LOGIN + " TEXT PRIMARY KEY," + KEY_PASSWORD + " TEXT,"
                + KEY_SCORE + " INTEGER" + ")")
        db?.execSQL(CREATE_PLAYERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
        onCreate(db)
    }

    fun addPlayer(emp: EmpModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_LOGIN, emp.login)
        contentValues.put(KEY_PASSWORD, emp.password)
        contentValues.put(KEY_SCORE, emp.score )

        val success = db.insert(TABLE_PLAYERS, null, contentValues)

        db.close()
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun viewPlayer():List<EmpModelClass>{
        val empList:ArrayList<EmpModelClass> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_PLAYERS ORDER BY score DESC LIMIT 10"
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var login: String
        var password: String
        var score: Int
        if (cursor.moveToFirst()) {
            do {
                login = cursor.getString(cursor.getColumnIndex("login"))
                password = cursor.getString(cursor.getColumnIndex("password"))
                score = cursor.getInt(cursor.getColumnIndex("score"))
                val emp= EmpModelClass(login = login, password = password, score = score)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    @SuppressLint("Range", "Recycle")
    fun findPlayer(name: String):List<EmpModelClass>{
        val empList:ArrayList<EmpModelClass> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_PLAYERS WHERE login = '$name'"
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var login: String
        var password: String
        var score: Int
        if (cursor.moveToFirst()) {
            do {
                login = cursor.getString(cursor.getColumnIndex("login"))
                password = cursor.getString(cursor.getColumnIndex("password"))
                score = cursor.getInt(cursor.getColumnIndex("score"))
                val emp= EmpModelClass(login = login, password = password, score = score)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    fun updatePlayer(emp: EmpModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_LOGIN, emp.login)
        contentValues.put(KEY_PASSWORD, emp.password)
        contentValues.put(KEY_SCORE,emp.score )

        val success = db.update(TABLE_PLAYERS, contentValues,"login='"+emp.login+"'",null)
        db.close()
        return success
    }
}