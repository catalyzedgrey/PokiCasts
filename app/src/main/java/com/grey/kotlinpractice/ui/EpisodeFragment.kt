package com.grey.kotlinpractice.ui

//import com.grey.kotlinpractice.di.component.DaggerExoPlayerComponent
//import com.grey.kotlinpractice.di.component.DaggerViewModelComponent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.grey.kotlinpractice.PodcastPlayer
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.EpisodeAdapter
import com.grey.kotlinpractice.data.Model
import com.squareup.picasso.Picasso
import tw.ktrssreader.model.channel.ITunesChannelData
import tw.ktrssreader.model.item.ITunesItemData


class EpisodeFragment : Fragment(), EpisodeAdapter.PlayButtonClickedListener  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EpisodeAdapter
    private var itemList = ArrayList<ITunesItemData>()
    private var podIndex: String = ""
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var podIcon: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episode_list, container, false)
    }


    private fun setUpFrag() {
        val m = activity as MainActivity
        //m.test()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = manager
        adapter = EpisodeAdapter(view.context, itemList)
        adapter.setOnPlayButtonClickedListener(this)
        recyclerView.adapter = adapter

        podIcon = view.findViewById(R.id.podIconControl)

        updateRecyclerViewResults()
        observeRssData()

    }



    private fun updateRecyclerViewResults() {
        if (podIndex != "") {
            updateListing()
            val res: Model.Results = viewModel.data.value!!
            Log.v("fragTest", res.results[podIndex.toInt()].feedUrl)
            Picasso.get().load(res.results[podIndex.toInt()].artworkUrl600).resize(450, 450)
                .into(podIcon)

        }
    }

    private fun observeRssData() {
        val resultObserver = Observer<ITunesChannelData> { result ->
            // Update the UI
            Log.v("XML", "xml result from frag--------")
            itemList = result.items as ArrayList<ITunesItemData>
            adapter.updateList(itemList)
            recyclerView.adapter = adapter
        }
        viewModel.rssData?.observe(viewLifecycleOwner, resultObserver)
    }

    private fun updateListing() {
        //var podcast: Model.Podcast = res.results[podIndex.toInt()]
        viewModel.getXMLResult(podIndex.toInt())
    }

    fun updatePodcastIndex(index: String) {
        podIndex = index
    }

    fun getAdapter(): EpisodeAdapter{
        return adapter
    }

    override fun sendPodcastUri(uri: String, title: String) {

        PodcastPlayer.preparePlayer(uri, title)
        //val m = activity as MainActivity
        //m.preparePlayer(uri)
    }

//    fun setButtonImageResource(clickedUri: String, position: Int, holder: EpisodeAdapter.MyViewHolder) {
//
//        if (!PodcastPlayer.isPlaying()) {
//            holder.playBtn.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp)
//        } else if (PodcastPlayer.isPlaying() && clickedUri != PodcastPlayer.getCurrentUri()) {
//            holder.
//            Log.v("clickListener", PodcastPlayer.getCurrentUri())
//        } else {
//            imgView.setImageResource(R.drawable.ic_play_circle_filled_white_24dp)
//        }
//        previousPlayingId = imgView.id
//    }

}