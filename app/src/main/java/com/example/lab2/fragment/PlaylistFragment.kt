package com.example.lab2.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.lab2.R
import com.example.lab2.activity.TrackDetailActivity
import com.example.lab2.model.Track
import com.example.lab2.viewmodel.TrackViewModel
import kotlinx.android.synthetic.main.activity_track_detail.view.*
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.playlist_list_item.view.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
@SuppressLint("ValidFragment")
class PlaylistFragment(context: Context) : Fragment() {

    private var adapter = PlaylistAdapter()
    private var parentContext: Context = context
    private lateinit var viewModel: TrackViewModel

    private var trackList: ArrayList<Track> = ArrayList()

    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return layoutInflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onStart() {
        super.onStart()
        //creates grid view layout
        playlist_list.layoutManager = LinearLayoutManager(parentContext)
        playlist_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        val observer = Observer<ArrayList<Track>> {
            playlist_list.adapter = adapter
            //manage results
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                    if (p0>=trackList.size || p1 >=trackList.size)
                    {
                        return true
                    }
                    else
                    {
                        return trackList[p0].gettrackurl() == trackList[p1].gettrackurl()
                    }
                }

                override fun getOldListSize(): Int {
                    return trackList.size
                }

                override fun getNewListSize(): Int {
                    if (it == null) {
                        return 0
                    }
                    return it.size
                }

                //checks list contents
                override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                    if (p0>=trackList.size || p1 >=trackList.size)
                    {
                        return true
                    }
                    else
                    {
                        return trackList[p0] == trackList[p1]
                    }
                }
            })
            result.dispatchUpdatesTo(adapter)
            trackList = it ?: ArrayList()
        }

        viewModel.getPlaylist().observe(this, observer)
    }


    inner class PlaylistAdapter: RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PlaylistViewHolder {
            val itemView = LayoutInflater.from(p0.context).inflate(R.layout.playlist_list_item, p0, false)
            return PlaylistViewHolder(itemView)
        }

        override fun onBindViewHolder(p0: PlaylistViewHolder, p1: Int) {
            val product = trackList[p1]

            p0.songTitle.text = product.gettrackname()
            p0.artistName.text = product.getartistname()


            //makes it so the items are clickable
            p0.row.setOnClickListener {
                val intent = Intent(this@PlaylistFragment.parentContext, TrackDetailActivity::class.java)
                intent.putExtra("PRODUCT", product)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return trackList.size
        }

        //assigns the parts to the ui
        inner class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var row = itemView

            var songTitle: TextView = itemView.song_title1;
            var artistName: TextView = itemView.artist_name;

        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
