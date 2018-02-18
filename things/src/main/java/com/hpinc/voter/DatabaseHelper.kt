package com.hpinc.voter

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private var context: Context? = null
    private lateinit var userName: String
    private lateinit var userPass: String
    private lateinit var userPhno: String

    init {
        this.context = context
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table if not exists LOGGER (first_name TEXT,middle_name TEXT,last_name TEXT,age NUMBER,address TEXT,constitute TEXT,occupation TEXT,password TEXT,confirm TEXT,phone NUMBER,Register NUMBER, voted TEXT, castedTo TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when(oldVersion)
        {
            1 -> {
                db.execSQL("alter table LOGGER add column voted TEXT")
                db.execSQL("alter table LOGGER add column castedTo TEXT")
            }
        }
    }


    fun getyourdata2(user: String, pass: String): Int {
        val sb = this.readableDatabase
        //SELECT
        val columns = arrayOf("Register")

        //WHERE clause
        val selection = "first_name=? AND password=?"
        userName = user
        userPass = pass
        //WHERE clause arguments
        val selectionArgs = arrayOf(userName, userPass)
        val c: Cursor
        val data: Int
        val no_such_data = 0
        try {
            //SELECT name FROM login WHERE username=userName AND password=userPass
            c = sb.query("LOGGER", columns, selection, selectionArgs, null, null, null)
            c.moveToFirst()

            val i = c.count
            data = c.getInt(c.getColumnIndex("Register"))
            c.close()
            return if (i <= 0) {

                no_such_data
            } else data
        } catch (e: Exception) {
            e.printStackTrace()
            return no_such_data
        }

    }

    fun getyourdata3(phno: String): Int {
        val sb = this.readableDatabase
        //SELECT
        val columns = arrayOf("first_name")

        //WHERE clause
        val selection = "phone=? "

        userPhno = phno
        //WHERE clause arguments
        val selectionArgs = arrayOf(userPhno)
        val c: Cursor

        try {
            //SELECT Name FROM LOGGER WHERE phno=userPhno;
            c = sb.query("LOGGER", columns, selection, selectionArgs, null, null, null)
            c.moveToFirst()

            val i = c.count
            //data = c.getString(c.getColumnIndex("Name"));
            c.close()
            return if (i <= 0) {

                1
            } else {
                -1
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }

    }
    fun getVotedStatus(user: String, pass: String): String {
        val sb = this.readableDatabase
        //SELECT
        val columns = arrayOf("voted")

        //WHERE clause
        val selection = "first_name=? AND password=?"
        userName = user
        userPass = pass
        //WHERE clause arguments
        val selectionArgs = arrayOf(userName, userPass)
        val c: Cursor
        val data: String
        val no_such_data = "true"
        try {
            //SELECT name FROM login WHERE username=userName AND password=userPass
            c = sb.query("LOGGER", columns, selection, selectionArgs, null, null, null)
            c.moveToFirst()

            val i = c.count
            data = c.getString(c.getColumnIndex("voted"))
            c.close()
            return if (i <= 0) {

                no_such_data
            } else data
        } catch (e: Exception) {
            e.printStackTrace()
            return no_such_data
        }

    }
    companion object {
        private val DB_VERSION = 2
        private val DB_NAME = "Vot5.db"
    }
}
