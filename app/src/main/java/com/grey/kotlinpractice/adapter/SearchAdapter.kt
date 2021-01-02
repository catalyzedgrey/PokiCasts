package com.grey.kotlinpractice.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.data.PodcastDao
import com.grey.kotlinpractice.data.Repository
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchAdapter(
    private val context: Context,
    private var allResults: ArrayList<Model.Podcast>,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {
    lateinit var mCallback: SubscribeButtonClickedListener


    interface SubscribeButtonClickedListener {
        fun sendPodcastInfo(pod: Model.Podcast)
    }

    fun setOnSubscribeButtonClickedListener(callback: SubscribeButtonClickedListener) {
        this.mCallback = callback
    }

    fun sendPodcastInfo(pod: Model.Podcast){
        mCallback.sendPodcastInfo(pod)
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val podIcon: ImageView = view.findViewById(R.id.icon)
        val artistName: TextView = view.findViewById(R.id.name)
        val title: TextView = view.findViewById(R.id.title)
        val subscribeBtn: ImageView = view.findViewById(R.id.subscribe_btn)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(newList: ArrayList<Model.Podcast>) {
        allResults = newList
        notifyDataSetChanged()
    }


    //Create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return MyViewHolder(view)
    }

    //Replace the contents of a view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Picasso.get().load(allResults[position].artworkUrl600!!).resize(200,200).into(holder.podIcon)
        holder.artistName.text = allResults[position].artistName
        holder.title.text = allResults[position].collectionName

        val podcastDao = Repository.DatabaseHandler.db.podcastDao()
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        coroutineScope.launch {
            val pod: Model.Podcast? = podcastDao.findByFeedUrl(allResults[position].feedUrl)

            if(pod != null && allResults[position].feedUrl == pod.feedUrl)
                holder.subscribeBtn.setImageResource(R.drawable.ic_baseline_check_circle_24)
        }






        holder.subscribeBtn.setOnClickListener{
            holder.subscribeBtn.setImageResource(R.drawable.ic_baseline_check_circle_24)
            //sendPodcastInfo(Model.Podcast())
            viewModel.subscribeToPodcast(allResults[position])


        }
    }



    override fun getItemCount(): Int {
        return allResults.size
    }


}
