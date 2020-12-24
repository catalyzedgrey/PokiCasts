package com.grey.kotlinpractice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.Model

class EpisodeAdapter( private val context: Context, private var allResults: ArrayList<Model.Ep>) : RecyclerView.Adapter<EpisodeAdapter.MyViewHolder>() {

    class MyViewHolder(var view: View): RecyclerView.ViewHolder(view){
        val episodeReleaseDate: TextView = view.findViewById(R.id.rel_date)
        val episodeTitle: TextView = view.findViewById(R.id.episode_title)
        val playBtn: ImageView = view.findViewById(R.id.play_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.podcast_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: EpisodeAdapter.MyViewHolder, position: Int) {
        holder.episodeReleaseDate.text = allResults[position].pubDate
        holder.episodeTitle.text = allResults[position].title
    }

    override fun getItemCount(): Int {
        return  allResults.size
    }
}