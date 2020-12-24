//package com.grey.kotlinpractice.adapter
//
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.grey.kotlinpractice.R
//import com.grey.kotlinpractice.data.Model
//import com.grey.kotlinpractice.ui.EpisodeFragment
//import com.squareup.picasso.Picasso
//
//class PodcastHomeAdapter(
//    private val context: Context,
//    private var allResults: ArrayList<Model.Podcast>
//
//) : RecyclerView.Adapter<PodcastHomeAdapter.MyViewHolder>() {
//    lateinit var mCallback: onItemClickedListener
//
//    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
//        val podIcon: ImageView = view.findViewById(R.id.icon)
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    fun updateList(newList: ArrayList<Model.Podcast>) {
//        allResults = newList
//        notifyDataSetChanged()
//    }
//
//
//    //Create new views
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
//
//        return MyViewHolder(view)
//    }
//
//    //Replace the contents of a view
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val url: String = allResults[position].artworkUrl600!!
//        Picasso.get().load(url).into(holder.podIcon)
//
//    }
//
//    override fun getItemCount(): Int {
//        return allResults.size
//    }
//
////    private fun replaceFragment(episode: Model.Ep) {
////        var episodeFragment = EpisodeFragment()
////        var mBundle = Bundle()
////        mBundle.putParcelable("item_Selected_key", episode)
////
////    }
//
//    interface onItemClickedListener {
//        fun sendText(text: String)
//    }
//
//    fun setOnItemClickedListener(callback: onItemClickedListener) {
//        this.mCallback = callback
//    }
//
//    fun yourMethodofSendingText() {
//        //here you can get the text from the edit text or can use this method according to your need
//        mCallback.sendText("YOUR TEXT")
//    }
//}
//
//
