package com.example.lab2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.util.Log
import com.example.lab2.db.DbSettings
import com.example.lab2.db.PlaylistDatabaseHelper
import com.example.lab2.model.Track
import com.example.lab2.util.QueryUtils

//Async helpers

class TrackViewModel(application: Application): AndroidViewModel(application) {
    private var _playlistdbhelper: PlaylistDatabaseHelper = PlaylistDatabaseHelper(application)
    private var _tracklist: MutableLiveData<ArrayList<Track>> = MutableLiveData()

    fun getTopTracks(query: String): MutableLiveData<ArrayList<Track>> {
        TrackAsyncTask().execute(query)
        return _tracklist
    }

    fun getTopTracksByQueryText(query: String): MutableLiveData<ArrayList<Track>> {
        TrackAsyncTask().execute(query)
        return _tracklist
    }


    @SuppressLint("StaticFieldLeak")
    inner class TrackAsyncTask: AsyncTask<String, Unit, ArrayList<Track>>() {
        override fun doInBackground(vararg params: String?): ArrayList<Track>? {
            Log.e("LOOK AT THIS::!!!! ", params[0]!!)
            return QueryUtils.fetchTrackData(params[0]!!)
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            if (result == null) {
                Log.e("RESULTS", "No Results Found")
            }
            else {
                var playlist = loadPlaylist()
                var list = arrayListOf<Track>()

                //check playlist status

                for (res in result)
                {
                    for (trak in playlist)
                    {
                        if (res.gettrackurl()==trak.gettrackurl())
                        {
                            res.isInPlaylist = true;
                            break;
                        }
                    }
                    list.add(res)
                }

                _tracklist.value = result
            }
        }
    }

    //get
    fun getPlaylist(): MutableLiveData<ArrayList<Track>> {
        val returnList = this.loadPlaylist()
        this._tracklist.value = returnList
        return this._tracklist
    }


    //load

    private fun loadPlaylist(): ArrayList<Track> {
        val tracks: ArrayList<Track> = ArrayList()
        val database = this._playlistdbhelper.readableDatabase

        //load from database
        val cursor = database.query(
            DbSettings.DBPlaylistEntry.TABLE,
            null,
            null, null, null, null, DbSettings.DBPlaylistEntry.TRACK_NAME
        )


        while (cursor.moveToNext()) {
            val cursorTrackName = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.TRACK_NAME)
            val cursorArtistName = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.ARTIST)
            val cursorImageUrl = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.IMAGE_URL)
            val cursorPlaycount = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.PLAYCOUNT)
            val cursorListeners = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.LISTENERS)
            val cursorTrackUrl = cursor.getColumnIndex(DbSettings.DBPlaylistEntry.TRACK_URL)

            val product = Track(
                cursor.getString(cursorTrackName),
                cursor.getString(cursorArtistName),
                cursor.getString(cursorImageUrl),
                cursor.getInt(cursorPlaycount),
                cursor.getString(cursorTrackUrl),
                cursor.getInt(cursorListeners)
                )
            product.isInPlaylist = true
            tracks.add(product)
        }

        cursor.close()

        database.close()

        return tracks
    }

    //add to database
    fun addTrackToPlaylist(track: Track) {
        val database: SQLiteDatabase = this._playlistdbhelper.writableDatabase

        val trackValues = ContentValues()
        trackValues.put(DbSettings.DBPlaylistEntry.TRACK_NAME, track.gettrackname())
        trackValues.put(DbSettings.DBPlaylistEntry.TRACK_URL, track.gettrackurl())
        trackValues.put(DbSettings.DBPlaylistEntry.ARTIST, track.getartistname())
        trackValues.put(DbSettings.DBPlaylistEntry.IMAGE_URL, track.getimageurl())
        trackValues.put(DbSettings.DBPlaylistEntry.LISTENERS, track.getlisteners())
        trackValues.put(DbSettings.DBPlaylistEntry.PLAYCOUNT, track.getplaycount())
        Log.e("Tried to add track:", track.gettrackname())


        database.insertWithOnConflict(
            DbSettings.DBPlaylistEntry.TABLE,
            null,
            trackValues,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        database.close()


    }

    //remove from database


    fun removeFromPlaylist(trackurl: String, isFromResultList: Boolean = false) {
        val database: SQLiteDatabase = this._playlistdbhelper.writableDatabase

        database.delete(DbSettings.DBPlaylistEntry.TABLE, "track_url = ?", arrayOf(trackurl));
        database.close()

        var copy = _tracklist.value;


        //had to initialize it to something

        if (copy != null) {
            var index: Track? = null;
            for (i in copy) {
                if (i.gettrackurl()==trackurl) {
                    //record index
                    index = i
                }
            }

            if (isFromResultList)
            {
                if (index != null) {
                    index.isInPlaylist=false
                };
            }
            else
            {

                copy.remove(index)
            }

            _tracklist.value=copy
        }
    }
}