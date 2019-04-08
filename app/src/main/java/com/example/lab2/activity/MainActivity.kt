package com.example.lab2.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import com.example.lab2.R
import com.example.lab2.fragment.HomeFragment
import com.example.lab2.fragment.PlaylistFragment
import kotlinx.android.synthetic.main.activity_main.*

//main activty
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //for tabs
        val fragmentAdapter = MyPagerAdapter(supportFragmentManager)
        viewpager_main.adapter = fragmentAdapter
        tabs_main.setupWithViewPager(viewpager_main)

    }

//for tabs
    inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    //home page
                    HomeFragment(this@MainActivity)
                }
                else ->
                    //playlist page
                    PlaylistFragment(this@MainActivity)

            }
        }

        override fun getCount(): Int {
            return 2
        }

        //page title
        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Home"
                else -> {
                    return "Playlist"
                }
            }
        }
    }
}
