package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.EpisodeAdapter
import com.grey.kotlinpractice.adapter.SearchAdapter
import com.grey.kotlinpractice.data.Model

class EpisodeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EpisodeAdapter
    private val itemList = ArrayList<Model.Ep>()
    private var podIndex: String = ""
    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var res: Model.Results

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episode_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val temp1 = Model.Ep("test1", "test2", "test3", "test4", "test5")
        itemList.add(temp1)
        super.onViewCreated(view, savedInstanceState)
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = manager
        adapter = EpisodeAdapter(view.context, itemList)
        recyclerView.adapter = adapter


        var res: Model.Results = viewModel.data.value!!
        if (podIndex != "") {
            updateListing()
            Log.v("fragTest", res.results[podIndex.toInt()].feedUrl)

        }


    }

    fun updateListing() {
        var podcast: Model.Podcast = res.results[podIndex.toInt()]

    }

    fun updatePodcastIndex(index: String) {
        podIndex = index
    }


}