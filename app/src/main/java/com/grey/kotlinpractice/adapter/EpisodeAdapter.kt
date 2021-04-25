package com.grey.kotlinpractice.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.grey.kotlinpractice.PodcastPlayerService
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.utils.Util


class EpisodeAdapter(
    private val context: Context,
//    private var itemList: ArrayList<ITunesItemData>,
    private var itemList: ArrayList<Model.Episode>,
    private var podcastPlayerService: PodcastPlayerService
) : RecyclerView.Adapter<EpisodeAdapter.MyViewHolder>() {
    lateinit var mCallback: PlayButtonClickedListener
    lateinit var episodeCallback: EpisodeClickedListener
    var currentPosition = -1
    //lateinit var artistName: String


    interface PlayButtonClickedListener {
        fun sendPodcastUri(uri: String)
    }

    interface EpisodeClickedListener {

        fun sendPodcastEpisodeInfo(episode: Model.Episode)
    }

    fun setOnPlayButtonClickedListener(callback: PlayButtonClickedListener) {
        this.mCallback = callback
    }

    fun setOnEpisodeClickedListener(callback: EpisodeClickedListener) {
        this.episodeCallback = callback
    }

    private fun sendPodcastEpisodeInfo(uri: String) {
        //here you can get the text from the edit text or can use this method according to your need
        mCallback.sendPodcastUri(uri)
    }

    private fun sendEpisodeInfo(episode: Model.Episode) {
        //here you can get the text from the edit text or can use this method according to your need
        episodeCallback.sendPodcastEpisodeInfo(episode)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.podcast_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EpisodeAdapter.MyViewHolder, position: Int) {

        podcastPlayerService.addListener(holder)

        holder.episodeReleaseDate.text = Util.stripTimeFromDateString(itemList[position].pubDate!!)
        holder.episodeTitle.text = itemList[position].title
        holder.duration.text = itemList[position].duration
//        holder.uri = itemList[position].enclosure!!.url!!
        holder.uri = itemList[position].url!!

        checkIfPlayed(holder, position)


        holder.itemView.setOnClickListener {
            sendEpisodeInfo(itemList[position])
        }
        //holder.uri = itemList[position].enclosure!!.url!!


        holder.playBtn.setOnClickListener {
            currentPosition = position
            holder.podcastPlayerService = podcastPlayerService
            podcastPlayerService.updateCurrentPlayingEpisode(itemList[position])
            itemList[position].isMarkedPlayed = false
            checkIfPlayed(holder, position)
//            sendPodcastEpisodeInfo(itemList[position].enclosure!!.url!!)
            sendPodcastEpisodeInfo(itemList[position].url!!)
//            podcastPlayerService.artistTitle = itemList[position].collectionName!!
//            podcastPlayerService.episodeTitle = holder.episodeTitle.text.toString()
//            podcastPlayerService.artworkUrl = itemList[position].imageUrl!!
        }
    }

    private fun checkIfPlayed(holder: EpisodeAdapter.MyViewHolder, position: Int) {
        if(itemList[position].isMarkedPlayed == true){
            var c = ContextCompat.getColor(context, R.color.playedText)
            holder.episodeTitle.setTextColor(c)
            holder.episodeReleaseDate.setTextColor(c)
            holder.duration.setTextColor(c)
        }else{
            holder.episodeTitle.setTextColor(Color.WHITE)
            holder.episodeReleaseDate.setTextColor(Color.WHITE)
            holder.duration.setTextColor(Color.WHITE)
        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }


//    fun updateList(newList: ArrayList<ITunesItemData>) {
//        itemList.clear()
//        itemList = newList
//        notifyDataSetChanged()
//    }


    fun updateList(newList: ArrayList<Model.Episode>) {
        itemList.clear()
        itemList = newList
        notifyDataSetChanged()
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view), Player.EventListener {
        val episodeReleaseDate: TextView = view.findViewById(R.id.rel_date)
        val episodeTitle: TextView = view.findViewById(R.id.episode_title)
        val duration: TextView = view.findViewById(R.id.duration)
        val playBtn: ImageView = view.findViewById(R.id.play_btn)
        var uri: String = ""
        var podcastPlayerService: PodcastPlayerService? = null


        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying && uri == podcastPlayerService?.currentUri) {
                playBtn.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp)
            } else if(!isPlaying && uri == podcastPlayerService?.currentUri){
                playBtn.setImageResource(R.drawable.ic_play_circle_filled_white_24dp)
            }


        }

    }

}
