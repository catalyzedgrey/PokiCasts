package com.grey.kotlinpractice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.palette.graphics.Palette
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


object PodcastPlayer : Player.EventListener {

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var mediaItem: MediaItem
    private var currentUri: String = ""
    private var episodeTitle: String = ""
    private var artistTitle: String = ""
    var artworkUrl: String = ""
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var artwork: Bitmap
    lateinit var paletteSwatch: Palette.Swatch

    private var playerNotificationManager: PlayerNotificationManager? = null
    private val mediaDescriptionAdapter: MediaDescriptionAdapter =
        object : MediaDescriptionAdapter {
            override fun getCurrentSubText(player: Player): String? {
                return "null"
            }

            override fun getCurrentContentTitle(player: Player): String {
                return artistTitle
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): String? {
                return episodeTitle
            }

            override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
                return artwork
            }
        }


    fun initPlayer(context: Context) {
        exoPlayer = SimpleExoPlayer.Builder(context).build()
        exoPlayer.addListener(this)



        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            "My_channel_id",
            R.string.nameResourceId,
            R.string.nameResourceId,
            R.string.nameResourceId,
            mediaDescriptionAdapter
        )



        playerNotificationManager!!.setPlayer(exoPlayer)
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
            play()
        } else if (currentUri == uri && isPlaying())
            pause()
        else if (currentUri == uri && !isPlaying())
            play()

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if(isPlaying){
            playerNotificationManager!!.setColor(paletteSwatch.rgb)
        }
    }

    fun play() {
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


    fun onDestroy() {
        if (playerNotificationManager != null) {
            playerNotificationManager!!.setPlayer(null)
        }
        if (exoPlayer != null) {
            exoPlayer.release()
            //exoPlayer = null
        }
    }


    fun loadBitmap() {


        Picasso.get().load(artworkUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                artwork = bitmap!!
                paletteSwatch = createPaletteSync(artwork).dominantSwatch!!

            }
        })


    }

    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
}


