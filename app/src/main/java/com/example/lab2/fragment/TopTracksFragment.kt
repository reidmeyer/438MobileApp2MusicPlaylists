package com.example.lab2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.lab2.activity.TrackDetailActivity
import com.example.lab2.model.Track
import com.example.lab2.viewmodel.TrackViewModel
import kotlinx.android.synthetic.main.activity_track_detail.view.*
import kotlinx.android.synthetic.main.fragment_top_tracks.*
import java.util.*
import android.support.v7.widget.GridLayoutManager
import com.example.lab2.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.top_tracks_list_item.view.*


@SuppressLint("ValidFragment")
class TopTracksFragment(context: Context): Fragment() {
    private var adapter = NewProductAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: TrackViewModel

    private var productList: ArrayList<Track> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_top_tracks, container, false)
    }

    override fun onStart() {
        super.onStart()
        //grid layout setup
        top_tracks_list.layoutManager = (GridLayoutManager(parentContext, 2))
        //top_tracks_list.layoutManager = LinearLayoutManager(parentContext)
        top_tracks_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            top_tracks_list.adapter = adapter
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    if (p0>=productList.size || p1 >=productList.size)
                    {
                        return true
                    }
                    else
                    {
                        return productList[p0].gettrackurl() == productList[p1].gettrackurl()
                    }
                }

                override fun getOldListSize(): Int {
                    return productList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    if (p0>=productList.size || p1 >=productList.size)
                    {
                        return true
                    }
                    else
                    {
                        return productList[p0] == productList[p1]
                    }
                }
            })
            result.dispatchUpdatesTo(adapter)
            productList = it ?: ArrayList()
        }

        //api call
        viewModel.getTopTracks("/?method=chart.gettoptracks&api_key=710f6edc67f5e4b1506979b31e2de36f&format=json").observe(this, observer)
    }

    inner class NewProductAdapter: RecyclerView.Adapter<NewProductAdapter.NewProductViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NewProductViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.top_tracks_list_item, p0, false)
            return NewProductViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: NewProductViewHolder, p1: Int) {
            val product = productList[p1]

            //set up ui

            if (product.getimageurl()!="")
            {
                Picasso.with(parentContext)?.load(product.getimageurl())?.into(p0.productImage)
            }

            p0.productTitle.text = product.gettrackname()
            p0.productArtist.text = product.getartistname()


            //make items clickable
            p0.row.setOnClickListener {
                val intent = Intent(this@TopTracksFragment.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("PRODUCT", product)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return productList.size
        }

        inner class NewProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView

            //set up ui variables
            var productImage: ImageView = itemView.product_img1

            var productTitle: TextView = itemView.product_title1
            var productArtist: TextView = itemView.product_artist1

        }
    }
}