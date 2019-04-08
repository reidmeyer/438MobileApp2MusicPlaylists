package com.example.lab2.util

import android.text.TextUtils
import android.util.Log
import com.example.lab2.model.Track
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import kotlin.collections.ArrayList

//api helper functions, all from studio

class QueryUtils {
    companion object {
        private val LogTag = this::class.java.simpleName
        private const val BaseURL = "http://ws.audioscrobbler.com/2.0" // localhost URL

        fun fetchTrackData(jsonQueryString: String): ArrayList<Track>? {

            if (jsonQueryString.contains("method=chart"))
            {
                val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

                var jsonResponse: String? = null
                try {
                    jsonResponse = makeHttpRequest(url)
                }
                catch (e: IOException) {
                    Log.e(this.LogTag, "Problem making the HTTP request.", e)
                }

                return extractDataFromJson(jsonResponse)
            }
            else
            {
                val url: URL? = createUrl("${this.BaseURL}$jsonQueryString")

                var jsonResponse: String? = null
                try {
                    jsonResponse = makeHttpRequest(url)
                }
                catch (e: IOException) {
                    Log.e(this.LogTag, "Problem making the HTTP request.", e)
                }

                return extractArtistTopTracksFromJson(jsonResponse)
            }


        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            }
            catch (e: MalformedURLException) {
                Log.e(this.LogTag, "Problem building the URL.", e)
            }

            return url
        }

        private fun makeHttpRequest(url: URL?): String {
            var jsonResponse = ""

            if (url == null) {
                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 10000 // 10 seconds
                urlConnection.connectTimeout = 15000 // 15 seconds
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                }
                else {
                    Log.e(this.LogTag, "Error response code: ${urlConnection.responseCode}")
                }
            }
            catch (e: IOException) {
                Log.e(this.LogTag, "Problem retrieving the product data results: $url", e)
            }
            finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }

            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }



        //for top tracks home screen
        private fun extractDataFromJson(productJson: String?): ArrayList<Track>? {
            if (TextUtils.isEmpty(productJson)) {
                return null
            }

            val top50 = ArrayList<Track>()
            try {
                val baseJsonResponse = JSONObject(productJson)
                val tracksObject = baseJsonResponse.getJSONObject("tracks")

                val trackArray = tracksObject.getJSONArray("track")
                for (i in 0 until trackArray.length())
                {

                    val trackObject = trackArray.getJSONObject(i)
                    val artistObject = trackObject.getJSONObject("artist")
                    val imageArray = trackObject.getJSONArray("image")
                    val imageObject = imageArray.getJSONObject(3)



                    Log.d(this.LogTag, i.toString())
                    top50.add(Track(
                        returnValueOrDefault<String>(trackObject, "name") as String,
                        returnValueOrDefault<String>(artistObject, "name") as String,
                        returnValueOrDefault<String>(imageObject, "#text") as String,
                        returnValueOrDefault<Int>(trackObject, "playcount") as Int,
                        returnValueOrDefault<String>(trackObject, "url") as String,
                        returnValueOrDefault<Int>(trackObject, "listeners") as Int

                    ))
                }
            }
            catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return top50
        }


        //for top tracks from artist search

        private fun extractArtistTopTracksFromJson(productJson: String?): ArrayList<Track>? {
            if (TextUtils.isEmpty(productJson)) {
                return null
            }

            val topArtistTracks = ArrayList<Track>()
            try {
                val baseJsonResponse = JSONObject(productJson)
                val tracksObject = baseJsonResponse.getJSONObject("toptracks")

                val trackArray = tracksObject.getJSONArray("track")
                for (i in 0 until trackArray.length())
                {

                    val trackObject = trackArray.getJSONObject(i)
                    val artistObject = trackObject.getJSONObject("artist")
                    val imageArray = trackObject.getJSONArray("image")
                    val imageObject = imageArray.getJSONObject(3)



                    Log.d(this.LogTag, i.toString())
                    topArtistTracks.add(Track(
                        returnValueOrDefault<String>(trackObject, "name") as String,
                        returnValueOrDefault<String>(artistObject, "name") as String,
                        returnValueOrDefault<String>(imageObject, "#text") as String,
                        returnValueOrDefault<Int>(trackObject, "playcount") as Int,
                        returnValueOrDefault<String>(trackObject, "url") as String,
                        returnValueOrDefault<Int>(trackObject, "listeners") as Int

                    ))
                }
            }
            catch (e: JSONException) {
                Log.e(this.LogTag, "Problem parsing the product JSON results", e)
            }

            return topArtistTracks
        }



        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        ""
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    }
                    else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    }
                    else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    }
                    else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    }
                    else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    }
                    else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }
    }
}