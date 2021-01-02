package com.grey.kotlinpractice.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.SearchAdapter
import com.grey.kotlinpractice.data.Model


class SearchFragment : Fragment() {

    private val itemList = ArrayList<Model.Podcast>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    lateinit var searchView: SearchView
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.searchForPodcast("waypoint")
        searchView = view.findViewById(R.id.searchView)
        searchView.setOnClickListener { searchView.isIconified = false }
        changeTextColor()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                viewModel.searchForPodcast(s)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = manager
        adapter = SearchAdapter(view.context, itemList, viewModel)
        recyclerView.adapter = adapter


        val resultObserver = Observer<Model.Results> { result ->
            // Update the UI
            adapter.updateList(result.results)

        }

        viewModel.searchForPodcast("").observe(viewLifecycleOwner, resultObserver)

//        val subscribedObserver = Observer<List<Model.Podcast>> { result ->
//            // Update the UI
//            if(result.isNotEmpty() || result.size != 0)
//                adapter.updateList(result as ArrayList<Model.Podcast>)
//                recyclerView.adapter = adapter
//
//        }
//
//        viewModel.subscribedPodList.observe(viewLifecycleOwner, subscribedObserver)



    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //tvCommon.text = "home frag"
        //commonLayout.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }

    private fun changeTextColor() {
        val id =
            searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = searchView.findViewById<View>(id) as TextView
        textView.setTextColor(Color.WHITE)
        textView.setHintTextColor(Color.WHITE);

    }
}


