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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class EpisodeFragment : Fragment(), EpisodeAdapter.PlayButtonClickedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EpisodeAdapter
    private var itemList = ArrayList<Model.Episode>()
    private var podIndex: String = ""
    private var artworkUrl: String = ""
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
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = manager
        adapter = EpisodeAdapter(view.context, itemList)
        adapter.setOnPlayButtonClickedListener(this)
        recyclerView.adapter = adapter

        podIcon = view.findViewById(R.id.mainpodIconControl)

        updateRecyclerViewResults()
        observeRssData()
    }
    private fun updateRecyclerViewResults() {
        if (podIndex != "") {
            updateListing()
            Picasso.get().load(artworkUrl).resize(450, 450)
                .into(podIcon)
        }
    }

    private fun observeRssData() {
        val resultObserver = Observer<List<Model.Episode>> { result ->
            // Update the UI

            adapter.updateList(result as ArrayList<Model.Episode>)
            itemList = result
        }
        viewModel.getEpisodeList(podIndex).observe(viewLifecycleOwner, resultObserver)

    }

    private fun updateListing() {


        //var podcast: Model.Podcast = res.results[podIndex.toInt()]


//        viewModel.getXMLResult(podIndex.toInt())
        //viewModel.getLocalXMLResult(podIndex)


    }

    fun updatePodcastIndex(index: String, artworkUrl: String) {
        podIndex = index
        this.artworkUrl = artworkUrl
    }

    override fun sendPodcastUri(uri: String) {
        PodcastPlayer.preparePlayer(uri)
    }


}