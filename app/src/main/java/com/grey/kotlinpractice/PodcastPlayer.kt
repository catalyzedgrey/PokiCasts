package com.grey.kotlinpractice

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar

object PodcastPlayer : Player.EventListener {

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var mediaItem: MediaItem
    private var currentUri: String = ""
    private var episodeTitle: String = ""
    private var artistTitle: String = ""


    fun initPlayer(context: Context) {
        exoPlayer = SimpleExoPlayer.Builder(context).build()
        exoPlayer.addListener(this)

    }

    fun preparePlayer(uri: String) {


        if (currentUri != uri) {


            exoPlayer.clearMediaItems()
            //stop()

            currentUri = uri
            mediaItem = MediaItem.fromUri(Uri.parse(uri))
            // Set the media item to be played.
            exoPlayer.setMediaItem(mediaItem)
            // Prepare the player.
            exoPlayer.prepare()
            // Start the playback.
            play(uri)
        } else if (currentUri == uri && isPlaying())
            pause()
        else if (currentUri == uri && !isPlaying())
            play(uri)

    }

    fun play(uri: String) {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    fun getCurrentUri(): String {
        return currentUri
    }

    fun getPlayer(): SimpleExoPlayer {

        return exoPlayer
    }

    fun addListener(listener: Player.EventListener) {
        exoPlayer.addListener(listener)
    }

    fun setEpisodeTitle(episodeTitle: String) {
        this.episodeTitle = episodeTitle
    }

    fun getEpisodeTitle(): String {
        return this.episodeTitle
    }

    fun setArtistTitle(artistTitle: String) {
        this.artistTitle = artistTitle
    }

    fun getArtistTitle(): String {
        return this.artistTitle
    }

    fun release() {
        exoPlayer.release()
    }


}


