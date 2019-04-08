package com.example.lab2.model

import java.io.Serializable


//Track object


class Track(): Serializable {
    // Identifiers


    //variables
    private var track_name: String = ""
    private var artist_name: String = ""
    private var image_url: String = ""
    private var playcount: Int = 0
    private var track_url: String = ""
    private var listeners: Int = 0

    //keep track of status
    var isInPlaylist: Boolean = false

    constructor(
        track_name: String,
        artist_name: String,
        image_url: String,
        playcount: Int,
        track_url: String,
        listeners: Int


        ) : this() {
        this.track_name = track_name
        this.artist_name = artist_name
        this.image_url = image_url
        this.playcount = playcount
        this.track_url = track_url
        this.listeners = listeners

    }


    //                        //
    // ------ Getters ------- //
    //                        //


    fun gettrackname(): String {
        return this.track_name
    }

    fun getartistname(): String {
        return this.artist_name
    }

    fun getimageurl(): String {
        return this.image_url
    }

    fun getplaycount(): Int {
        return this.playcount
    }

    fun gettrackurl(): String {
        return this.track_url
    }

    fun getlisteners(): Int {
        return this.listeners
    }

}