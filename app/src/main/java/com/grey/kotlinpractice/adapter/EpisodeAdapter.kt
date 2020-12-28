package com.grey.kotlinpractice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.grey.kotlinpractice.PodcastPlayer
import com.grey.kotlinpractice.R
import tw.ktrssreader.model.item.ITunesItemData


class EpisodeAdapter(
    private val context: Context,
    private var itemList: ArrayList<ITunesItemData>
) : RecyclerView.Adapter<EpisodeAdapter.MyViewHolder>() {
    lateinit var mCallback: PlayButtonClickedListener
    private var currentPosition = -1


//    override fun onIsPlayingChanged(isPlaying: Boolean) {
//        super.onIsPlayingChanged(isPlaying)
//        if (isPlaying) {
//            myViewHolder.playBtn.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp)
//        } else {
//            myViewHolder.playBtn.setImageResource(R.drawable.ic_play_circle_filled_white_24dp)
//        }
//
//    }

    interface PlayButtonClickedListener {
        fun sendPodcastUri(uri: String, title: String)
    }

    fun setOnPlayButtonClickedListener(callback: PlayButtonClickedListener) {
        this.mCallback = callback
    }

    private fun sendPodcastEpisodeInfo(uri: String, title: String) {
        //here you can get the text from the edit text or can use this method according to your need
        mCallback.sendPodcastUri(uri, title)
    }


    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view), Player.EventListener {
        val episodeReleaseDate: TextView = view.findViewById(R.id.rel_date)
        val episodeTitle: TextView = view.findViewById(R.id.episode_title)
        val duration: TextView = view.findViewById(R.id.duration)
        val playBtn: ImageView = view.findViewById(R.id.play_btn)
        var uri: String = ""

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying && uri == PodcastPlayer.getCurrentUri()) {
                playBtn.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp)
            } else if(uri == PodcastPlayer.getCurrentUri()){
                playBtn.setImageResource(R.drawable.ic_play_circle_filled_white_24dp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EpisodeAdapter.MyViewHolder, position: Int) {
        PodcastPlayer.setListener(holder)
        holder.episodeReleaseDate.text = itemList[position].pubDate
        holder.episodeTitle.text = itemList[position].title
        holder.duration.text = itemList[position].duration
        holder.uri = itemList[position].enclosure!!.url!!

        holder.playBtn.tag = 1;

        holder.playBtn.setOnClickListener {
            sendPodcastEpisodeInfo(itemList[position].enclosure!!.url!!,
                holder.episodeTitle.text as String)

//            val playStopButtonState = holder.playBtn.tag
//            var previousPosition = currentPosition
//
//            if (playStopButtonState == 1) {
//                currentPosition = holder.adapterPosition;
//                holder.playBtn.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
//                holder.playBtn.tag = 2;
//            } else {
//                currentPosition = -1;
//                holder.playBtn.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
//                holder.playBtn.tag = 1;
//            }
//            if (previousPosition != -1) {
//                notifyItemChanged(previousPosition);
//            }

        }

//        if (currentPosition == position) {
//            holder.playBtn.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
//        } else {
//            holder.playBtn.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
//        }


    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateList(newList: ArrayList<ITunesItemData>) {
        itemList = newList
        notifyDataSetChanged()
    }


}
