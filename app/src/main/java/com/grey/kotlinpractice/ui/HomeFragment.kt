package com.grey.kotlinpractice.ui

import android.os.Bundle
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

    interface ItemClickedListener {
        fun sendPodcastIndex(podcastPosIndex: String, artWork: String)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridView = view.findViewById(R.id.home_grid)
        adapter = HomeAdapter(view.context, R.layout.home_list_item, itemList)
        gridView.adapter = adapter


        gridView.setOnItemClickListener{ _, _, position, _ ->
            SendPodcastFeedUrl(itemList[position].feedUrl, itemList[position].artworkUrl600!!)
        }


        val gridObserver = Observer<List<Model.Podcast>> { result ->
            // Update the UI
            itemList = result as ArrayList<Model.Podcast>
            adapter.updateList(result as ArrayList<Model.Podcast>)
        }
        viewModel.getSubscribedPodcasts().observe(viewLifecycleOwner, gridObserver)


    }

    fun setOnItemClickedListener(callback: ItemClickedListener) {
        this.mCallback = callback
    }

    fun SendPodcastFeedUrl(podcastPosIndex: String, artWork: String) {
        //here you can get the text from the edit text or can use this method according to your need
        mCallback.sendPodcastIndex(podcastPosIndex, artWork)
    }
}