package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.HomeAdapter
//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.utils.Util
import okhttp3.internal.notify

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var itemList = ArrayList<Model.Podcast>()

    lateinit var mCallback: ItemClickedListener

//    lateinit var recyclerView: RecyclerView

    lateinit var gridView: GridView
//    lateinit var adapter: PodcastHomeAdapter

    lateinit var adapter: HomeAdapter
    private val viewModel: HomeViewModel by activityViewModels()

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    interface ItemClickedListener {
        fun sendPodcastIndex(podId: Int, podcastPosIndex: String, artWork: String, collectionName: String)
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

        gridView.numColumns = viewModel.homeIconSize

        gridView.setOnItemClickListener{ _, _, position, _ ->
            SendPodcastFeedUrl( itemList[position].uid, itemList[position].feedUrl, itemList[position].artworkUrl600!!, itemList[position].collectionName!!)
        }

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        val gridObserver = Observer<List<Model.Podcast>> { result ->
            // Update the UI
            itemList = result as ArrayList<Model.Podcast>
            sortGridViewDescendingly(viewModel.isSortingDesc)
        }
        viewModel.getSubscribedPodcasts().observe(viewLifecycleOwner, gridObserver)

    }

    fun sortGridViewDescendingly(isDescendingTrue: Boolean){
        if(itemList != null){
            if(isDescendingTrue){
                itemList.sortByDescending {
                    it.releaseDate
                }
            }else{
                itemList.sortBy {
                    it.releaseDate
                }

            }
            adapter.updateList(itemList as ArrayList<Model.Podcast>)
        }

    }

    fun setOnItemClickedListener(callback: ItemClickedListener) {
        this.mCallback = callback
    }

    fun SendPodcastFeedUrl(podId: Int, podcastPosIndex: String, artWorkUrl: String, collectionName: String) {
        //here you can get the text from the edit text or can use this method according to your need
        mCallback.sendPodcastIndex(podId, podcastPosIndex, artWorkUrl, collectionName)
    }

    override fun onRefresh() {
        if(!itemList.isNullOrEmpty())
            viewModel.getUpdatedPodcastResult(itemList[0].collectionId)
    }
}