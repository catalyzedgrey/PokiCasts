package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.CustomGridAdapter
import com.grey.kotlinpractice.data.Model

class HomeFragment : Fragment() {

    lateinit var homeViewModel: HomeViewModel
    private val itemList = ArrayList<Model.Podcast>()

    lateinit var recyclerView: RecyclerView
    lateinit var  adapter: CustomGridAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeViewModel = HomeViewModel()

        val gridObserver = Observer<Model.Results> { result ->
            // Update the UI
            Log.v("changing UI", "changing UI--------")
            adapter.updateList(result.results)
            recyclerView.adapter = adapter
        }
        homeViewModel.data.observe(viewLifecycleOwner, gridObserver)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager : RecyclerView.LayoutManager = GridLayoutManager(view.context, 3)
        recyclerView= view.findViewById(R.id.home_grid)
        recyclerView.layoutManager = manager
        adapter = CustomGridAdapter(view.context, itemList)
        recyclerView.adapter = adapter


        //homeViewModel.result.observe(viewLifecycleOwner)
//        homeViewModel.result.observe(viewLifecycleOwner) {
//            // update UI
//        }

        val resultObserver = Observer<Model.Results> { newResult ->
            // Update the UI, in this case, a TextView.

        }

        //homeViewModel.result.observe(viewLifecycleOwner, resultObserver)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //tvCommon.text = "home frag"
        //commonLayout.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
    }
}