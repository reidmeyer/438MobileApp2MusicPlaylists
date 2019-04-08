package com.example.lab2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.lab2.R
import kotlinx.android.synthetic.main.fragment_home.*


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
class HomeFragment(context: Context) : Fragment() {
    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    private var parentContext = context
    private var initialized: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }


    override fun onStart() {
        super.onStart()

        //list stuff. Makes TopTracksFragment the default

        if (!this.initialized) {
            val fm = fragmentManager
            val ft = fm?.beginTransaction()
            ft?.add(R.id.list_holder, TopTracksFragment(this.parentContext), "NEW_FRAG")
            ft?.commit()

            //Deals with search logic

            search_edit_text.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = search_edit_text.text
                    search_edit_text.setText("")
                    if (searchText.toString() == "") {
                        val toast = Toast.makeText(this.parentContext, "Please enter text", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        return@setOnEditorActionListener true
                    }
                    else {
                        performSearch(searchText.toString())
                        return@setOnEditorActionListener false
                    }
                }

                return@setOnEditorActionListener false
            }



            this.initialized = true
        }
    }

    //Search function
    private fun performSearch(query: String) {
        // Load Fragment into View
        val fm = fragmentManager

        // add
        val fragment = ResultListFragment(this.parentContext, query)
        val ft = fm?.beginTransaction()
        ft?.replace(R.id.list_holder, fragment, "RESULTS_FRAG")
        ft?.commit()
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
