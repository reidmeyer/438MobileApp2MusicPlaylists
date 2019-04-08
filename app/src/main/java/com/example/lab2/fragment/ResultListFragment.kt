package com.example.lab2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.lab2.R
import com.example.lab2.activity.TrackDetailActivity
import com.example.lab2.model.Track
import com.example.lab2.viewmodel.TrackViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_track_detail.*
import kotlinx.android.synthetic.main.activity_track_detail.view.*
import kotlinx.android.synthetic.main.fragment_result_list.*
import kotlinx.android.synthetic.main.result_list_item.view.*
import java.text.NumberFormat


@SuppressLint("ValidFragment")
class ResultListFragment(context: Context, query: String): Fragment() {
    private var adapter = ResultAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: TrackViewModel
    private var listInitialized = false

    private var queryString: String = query
    private var productList: ArrayList<Track> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        val displayText = "Search for: $queryString"
        (activity as AppCompatActivity).supportActionBar?.title = displayText

        result_items_list.layoutManager = (GridLayoutManager(parentContext, 2) as RecyclerView.LayoutManager?)

        result_items_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            result_items_list.adapter = adapter
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
                        return (productList[p0] == productList[p1])
                    }
                }
            })
            result.dispatchUpdatesTo(adapter)
            productList = it ?: ArrayList()
        }


        //main api call for the results from the query
        viewModel.getTopTracksByQueryText("/?method=artist.gettoptracks&artist="+queryString+"&api_key=710f6edc67f5e4b1506979b31e2de36f&format=json").observe(this, observer)


        this.listInitialized = true
    }

    inner class ResultAdapter: RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ResultViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.result_list_item, p0, false)
            return ResultViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: ResultViewHolder, p1: Int) {
            val product = productList[p1]

            //load the ui
            p0.productTitle.text = product.gettrackname()
            p0.productArtist.text = product.getartistname()


            if (product.getimageurl()!="")
            {
                Picasso.with(parentContext)?.load(product.getimageurl())?.into(p0.productImg)
            }


            p0.row.setOnClickListener {
                val intent = Intent(this@ResultListFragment.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("PRODUCT", product)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return productList.size
        }

        inner class ResultViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val row = itemView
            //set up the ui variables
            var productArtist: TextView = itemView.product_artist

            var productImg: ImageView = itemView.product_img
            var productTitle: TextView = itemView.product_title

        }
    }
}