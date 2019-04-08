package com.example.lab2.activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.example.lab2.model.Track
import com.example.lab2.viewmodel.TrackViewModel
import kotlinx.android.synthetic.main.activity_track_detail.*
import com.squareup.picasso.Picasso
import android.content.Intent
import com.example.lab2.R
import java.net.URLEncoder


//deals with the page that goes into the details of a specific track
class TrackDetailActivity: AppCompatActivity() {
    private lateinit var product: Track
    private lateinit var viewModel: TrackViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_detail)

        product = ((intent.extras.getSerializable("PRODUCT") as Track?)!!)
        Log.e("PLAYLIST", product.isInPlaylist.toString())

        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        //load ui
        this.loadUI(product)
    }

    //exit
    override fun onBackPressed() {
        this.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu, menu)
        if (this.product.isInPlaylist) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //favorites button
                menu?.getItem(0)?.icon = getDrawable(R.drawable.ic_star_24dp)
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            R.id.action_favorite -> {

                //deal with favorites button
                if (product.isInPlaylist)
                {
                    item.icon = getDrawable(R.drawable.ic_star_border_24dp)
                    viewModel.removeFromPlaylist(product.gettrackurl(), false)
                    product.isInPlaylist=false;
                }
                else{
                    item.icon = getDrawable(R.drawable.ic_star_24dp)
                    Log.e("Product name:", product.gettrackname())
                    viewModel.addTrackToPlaylist(product)
                    product.isInPlaylist=true;

                }

                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //based on https://www.c-sharpcorner.com/article/how-to-add-the-share-option-in-android-application/
    fun Clicked(view: View) {
        /* ACTION_SEND: Deliver some data to someone else.
        createChooser (Intent target, CharSequence title): Here, target- The Intent that the user will be selecting an activity to perform.
            title- Optional title that will be displayed in the chooser.
        Intent.EXTRA_TEXT: A constant CharSequence that is associated with the Intent, used with ACTION_SEND to supply the literal data to be sent.
        */
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, product.gettrackurl())
        sendIntent.type = "text/plain"
        Intent.createChooser(sendIntent, "Share via")
        startActivity(sendIntent)
    }

    //loads the ui
    private fun loadUI(product: Track) {

        product_title2.text = product.gettrackname()

        song_title.text = product.gettrackname()
        artist.text = product.getartistname()
        playcount.text = product.getplaycount().toString()
        url.text = product.gettrackurl()

        //https://www.rosettacode.org/wiki/URL_encoding#Kotlin
        var query = product.gettrackname() + " " + product.getartistname();
        query = URLEncoder.encode(query, "utf-8")

        //https://travishorn.com/link-directly-to-googles-i-m-feeling-lucky-feature-65ebe7b552bd
        listen.text = "http://www.google.com/search?q=youtube+"+query+"&btnI"

        if (product.getimageurl()!="")
        {
            Picasso.with(this).load(product.getimageurl()).into(product_img2)
        }



    }




}