package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.HomeAdapter
//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
import com.grey.kotlinpractice.data.Model

class HomeFragment : Fragment() {

    private var itemList = ArrayList<Model.Podcast>()

    lateinit var mCallback: ItemClickedListener

//    lateinit var recyclerView: RecyclerView

    lateinit var gridView: GridView
//    lateinit var adapter: PodcastHomeAdapter

    lateinit var adapter: HomeAdapter
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadResults("waypoint")
        gridView = view.findViewById(R.id.home_grid)
        adapter = HomeAdapter(view.context, R.layout.home_list_item, itemList)
        gridView.adapter = adapter


        gridView.setOnItemClickListener{parent, view, position, id ->
            SendPodcastIndex(position.toString())
        }
//        val manager: RecyclerView.LayoutManager = GridLayoutManager(view.context, 3)
//        recyclerView = view.findViewById(R.id.home_grid)
//        recyclerView.layoutManager = manager
//        adapter = PodcastHomeAdapter(view.context, itemList)
//        recyclerView.adapter = adapter





//        recyclerView.setOnClickListener {
//
//            Log.v("testing", "touched")
//        }


        val gridObserver = Observer<Model.Results> { result ->
            // Update the UI
            itemList = result.results
            adapter.updateList(result.results)
            gridView.adapter = adapter

//            recyclerView.adapter = adapter


        }
        viewModel.data.observe(viewLifecycleOwner, gridObserver)


    }


//    @JvmName("getAdapter1")
//    fun getAdapter(): PodcastHomeAdapter {
//        if(adapter!= null)
//            return adapter
//        return PodcastHomeAdapter(activity?.baseContext, itemList)
//    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //tvCommon.text = "home frag"
        //commonLayout.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }
    fun setOnItemClickedListener(callback: ItemClickedListener) {
        this.mCallback = callback
    }
    interface ItemClickedListener {
        fun sendPodcastIndex(podcastPosIndex: String)
    }
    fun SendPodcastIndex(podcastPosIndex: String) {
        //here you can get the text from the edit text or can use this method according to your need
        mCallback.sendPodcastIndex(podcastPosIndex)
    }
}