package com.example.lab2.db

import android.provider.BaseColumns

//based on studio
//sets up database for playlist
class DbSettings {
    companion object {
        const val DB_NAME = "playlist.db"
        const val DB_VERSION = 1
    }


    class DBPlaylistEntry: BaseColumns {
        companion object {
            const val TABLE = "playlist"
            const val ID = BaseColumns._ID


            //details tracked
            const val TRACK_NAME = "track_name"
            const val ARTIST = "artist"
            const val IMAGE_URL = "image_url"
            const val PLAYCOUNT = "playcount"
            const val TRACK_URL = "track_url"
            const val LISTENERS = "listeners"

        }
    }

}