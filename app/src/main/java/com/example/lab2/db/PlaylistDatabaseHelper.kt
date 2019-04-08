package com.example.lab2.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//based on studios
//sets up database
class PlaylistDatabaseHelper(context: Context): SQLiteOpenHelper(context, DbSettings.DB_NAME, null, DbSettings.DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createPlaylistTableQuery = "CREATE TABLE " + DbSettings.DBPlaylistEntry.TABLE + " ( " +
                DbSettings.DBPlaylistEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbSettings.DBPlaylistEntry.TRACK_NAME + " TEXT NULL, " +
                DbSettings.DBPlaylistEntry.ARTIST + " TEXT NULL, " +
                DbSettings.DBPlaylistEntry.IMAGE_URL + " TEXT NULL, " +
                DbSettings.DBPlaylistEntry.PLAYCOUNT + " INTEGER, " +
                DbSettings.DBPlaylistEntry.TRACK_URL + " TEXT NULL, " +
                DbSettings.DBPlaylistEntry.LISTENERS + " INTEGER);"

        db?.execSQL(createPlaylistTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + DbSettings.DBPlaylistEntry.TABLE)
        onCreate(db)
    }
}