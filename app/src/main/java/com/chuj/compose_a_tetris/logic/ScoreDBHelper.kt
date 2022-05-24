package com.chuj.compose_a_tetris.logic

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class ScoreDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    private val SQL_CREATE_ENTRYS =
        "CREATE TABLE ${ScoreContract.ScoreEntry.TABLE_NAME} (" +
            "${ScoreContract.ScoreEntry.COLUMN_TIME} INTEGER PRIMARY KEY," +
            "${ScoreContract.ScoreEntry.COLUMN_NAME} TEXT," +
            "${ScoreContract.ScoreEntry.COLUMN_SCORE} INTEGER)"

    private val SQL_DELETE_ENTRYS = "DROP TABLE IF EXISTS ${ScoreContract.ScoreEntry.TABLE_NAME}"

    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(SQL_CREATE_ENTRYS)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.execSQL(SQL_DELETE_ENTRYS)
        onCreate(p0)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertScore(record : ScoreContract.Record) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ScoreContract.ScoreEntry.COLUMN_SCORE, record.score)
            put(ScoreContract.ScoreEntry.COLUMN_TIME, record.currentTimeMillis)
            put(ScoreContract.ScoreEntry.COLUMN_NAME, record.name)
        }
        db.insert(ScoreContract.ScoreEntry.TABLE_NAME, null, values)
        db.close()
    }

    private fun rawSearch(selection : String) : MutableList<ScoreContract.Record> {
        val db = writableDatabase
        try {
            val cursor = db.query(
                ScoreContract.ScoreEntry.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
            )
            val records = mutableListOf<ScoreContract.Record>()
            with(cursor) {
                while (moveToNext()) {
                    val record = ScoreContract.Record(
                        getInt(getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_SCORE)),
                        getLong(getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_TIME)),
                        getString(getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_NAME))
                    )
                    records.add(record)
                }
            }
            return records
        } catch (e : SQLiteException) {
            db.close()
            return mutableListOf()
        }
    }

    fun searchScoreByName(name : String) : MutableList<ScoreContract.Record> {
        val selection = "${ScoreContract.ScoreEntry.COLUMN_NAME} = \"$name\""

        return rawSearch(selection)
    }

    fun searchScoreByTime(start : Long, end : Long) : MutableList<ScoreContract.Record> {
        val selection = "${ScoreContract.ScoreEntry.COLUMN_TIME} <= $end and " +
                "${ScoreContract.ScoreEntry.COLUMN_TIME} >= $start"

        return rawSearch(selection)
    }

    fun selectAll() : List<ScoreContract.Record> {
        val selection = "SELECT * FROM ${ScoreContract.ScoreEntry.TABLE_NAME}"
        val db = writableDatabase
        try {
            val cursor = db.rawQuery(selection, null)
            val records = mutableListOf<ScoreContract.Record>()
            with(cursor) {
                while (moveToNext()) {
                    val record = ScoreContract.Record(
                        getInt(getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_SCORE)),
                        getLong(getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_TIME)),
                        getString(getColumnIndexOrThrow(ScoreContract.ScoreEntry.COLUMN_NAME))
                    )
                    records.add(record)
                }
            }
            return records
        } catch (e : SQLiteException) {
            db.close()
            return mutableListOf()
        }
    }

    companion object {
        const val DATABASE_NAME = "tetris_score"
        const val DATABASE_VERSION = 1
    }
}