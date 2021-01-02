package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.PodcastPlayer
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.EpisodeAdapter
import com.grey.kotlinpractice.data.Model
import com.squareup.picasso.Picasso
import tw.ktrssreader.model.channel.ITunesChannelData
import tw.ktrssreader.model.item.ITunesItemData


class EpisodeFragment : Fragment(), EpisodeAdapter.PlayButtonClickedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EpisodeAdapter

        private var itemList = ArrayList<Model.Episode>()
//    lateinit var itemList: ArrayList<ITunesItemData>
    private var feedUrl: String = ""
    private var artworkUrl: String = ""
    private var collectionName: String = ""
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var podIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episode_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        itemList = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = manager
        adapter = EpisodeAdapter(view.context, itemList)
        adapter.setOnPlayButtonClickedListener(this)
        recyclerView.adapter = adapter
        podIcon = view.findViewById(R.id.mainpodIconControl)

        updateRecyclerViewResults()
        //observeRemoteEpisodeList()
        observeLocalEpisodeList()
    }

    private fun updateRecyclerViewResults() {
        if (feedUrl != "") {
            Picasso.get().load(artworkUrl).resize(450, 450)
                .into(podIcon)
            adapter.artistName = collectionName
        }
    }


    private fun observeLocalEpisodeList() {
        val resultObserver = Observer<List<Model.Episode>> { result ->
            // Update the UI
            if(result != null){
                adapter.updateList(result as ArrayList<Model.Episode>)
                itemList = result
            }

        }
        viewModel.getLocalEpisodeList(feedUrl).observe(viewLifecycleOwner, resultObserver)
    }


//    private fun observeRemoteEpisodeList() {
//        val resultObserver = Observer<ITunesChannelData> { result ->
//            // Update the UI
//            itemList = result.items as ArrayList<ITunesItemData>
//            adapter.updateList(itemList)
//
//        }
//        viewModel.getRemoteEpisodeList(feedUrl).observe(viewLifecycleOwner, resultObserver)
//    }


    fun updatePodcastIndex(feedUrl: String, artworkUrl: String, collectionName: String) {
        this.feedUrl = feedUrl
        this.artworkUrl = artworkUrl
        this.collectionName = collectionName
    }

    override fun sendPodcastUri(uri: String) {
        PodcastPlayer.preparePlayer(uri)
    }




}