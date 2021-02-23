package com.grey.kotlinpractice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.palette.graphics.Palette
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.*
import com.grey.kotlinpractice.data.AppDatabase
import com.grey.kotlinpractice.data.AppDatabase.DatabaseProvider.context
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.ui.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class PodcastPlayerService : Service(), Player.EventListener {

    var exoPlayer: SimpleExoPlayer?
    lateinit var mediaItem: MediaItem
    var currentUri: String = ""
    //var episodeTitle: String = ""
    //var artistTitle: String = ""
    //var artworkUrl: String = ""
    var artwork: Bitmap? = null

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var paletteSwatch: Palette.Swatch
    private val myBinder = MyLocalBinder()
    var playerNotificationManager: PlayerNotificationManager? = null

    lateinit var viewModel: HomeViewModel


    //private val mediaDescriptionAdapter: MediaDescriptionAdapter =
    lateinit var notification: Notification

    init {

        exoPlayer = SimpleExoPlayer.Builder(context).build()
        exoPlayer!!.addListener(this)


        configureNotification()
    }

    private fun configureNotification(){
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            "My_channel_id",
            R.string.nameResourceId,
            R.string.nameResourceId,
            R.string.nameResourceId,
            object : MediaDescriptionAdapter {
                override fun getCurrentSubText(player: Player): String? {
                    return null
                }

                override fun getCurrentContentTitle(player: Player): String {
                    return viewModel.currentEpisode?.collectionName ?: ""
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {

                    val intent = Intent(this@PodcastPlayerService, MainActivity::class.java)
                    intent.putExtra("shouldExpandSheet", true)
                    intent.putExtra("episodeTitle", viewModel.currentEpisode?.title ?: "")
                    intent.putExtra("artistTitle", viewModel.currentEpisode?.collectionName ?: "")

                    // both of these approaches now work: FLAG_CANCEL, FLAG_UPDATE; the uniqueInt may be the real solution.
                    //PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, showFullQuoteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    return PendingIntent.getActivity(
                        this@PodcastPlayerService,
                        50,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                override fun getCurrentContentText(player: Player): String {
                    return viewModel.currentEpisode?.title ?: ""
                }

                override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap {
                    if (artwork == null) {
                        loadBitmap()
                    }
                    return artwork!!
                }


            },
            object : NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    super.onNotificationPosted(notificationId, notification, ongoing)

                    if (viewModel.currentEpisode != null && exoPlayer?.playbackState == Player.STATE_ENDED) {
                        preparePlayer(
                            viewModel.currentEpisode!!.url!!,
                            viewModel.currentEpisode!!.currentPosition!!
                        )
                    }

                    if (ongoing)
                        startForeground(notificationId, notification);
                    else
                        stopForeground(false)
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    super.onNotificationCancelled(notificationId, dismissedByUser)
                    if (dismissedByUser)
                        stopSelf()
                }
            }
        )

        playerNotificationManager!!.setColorized(true)
        playerNotificationManager!!.setUsePreviousAction(false)
        playerNotificationManager!!.setUseNextAction(false)
        playerNotificationManager!!.setUseNextActionInCompactView(true)
        playerNotificationManager!!.setUsePreviousActionInCompactView(true)
        playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_HIGH)
        playerNotificationManager!!.setRewindIncrementMs(5000)
        playerNotificationManager!!.setFastForwardIncrementMs(5000)
        playerNotificationManager!!.setPlayer(exoPlayer)


    }

    fun clearMediaItem(){
        exoPlayer?.clearMediaItems()
    }

    fun updateCurrentPlayingEpisode(episode: Model.Episode) {
        viewModel.currentEpisode = episode
        loadBitmap()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initPlayer(context)

        return super.onStartCommand(intent, flags, startId)
    }

    fun initPlayer(context: Context) {
        exoPlayer = SimpleExoPlayer.Builder(AppDatabase.DatabaseProvider.context).build()
    }


    fun preparePlayer(uri: String, position: Long) {

        if (exoPlayer == null) {
            initPlayer(context)
        }

        if (currentUri != uri) {


            exoPlayer!!.clearMediaItems()
            //stop()

            currentUri = uri
            mediaItem = MediaItem.fromUri(Uri.parse(uri))
            // Set the media item to be played.
            exoPlayer!!.setMediaItem(mediaItem)
            // Prepare the player.
            exoPlayer!!.prepare()
            // Start the playback.

            exoPlayer!!.seekTo(position)
            play()
        } else if (currentUri == uri && isPlaying())
            pause()
        else if (currentUri == uri && !isPlaying())
            play()

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
//        if (!isPlaying && exoPlayer!!.playbackState == Player.STATE_IDLE) {
//            stopForeground(true)
//        }
    }

    fun play() {
        exoPlayer!!.play()
    }

    fun pause() {
        exoPlayer!!.pause()
    }

    fun stop() {
        exoPlayer!!.stop()
        stopForeground(true)
        kill()

    }

    fun kill(){
        if (playerNotificationManager != null) {
            playerNotificationManager!!.setPlayer(null)
            playerNotificationManager!!.invalidate()
        }
        if (exoPlayer != null) {
            exoPlayer!!.release()
            exoPlayer = null

        }
    }

    fun isPlaying(): Boolean {
        return exoPlayer!!.isPlaying
    }

    fun changePlaybackSpeed(speed: Float){
        val param = PlaybackParameters(speed)
        exoPlayer?.setPlaybackParameters(param)

    }


    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
//        if (!playWhenReady) {
//
//            stopForeground(true)
//        }
        super.onPlayWhenReadyChanged(playWhenReady, reason)
    }

    override fun onPlaybackStateChanged(state: Int) {
//        if (state == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
//            stopForeground(true)
        super.onPlaybackStateChanged(state)
    }

    //    fun getCurrentUri(): String {
//        return currentUri
//    }
//
//    fun getPlayer(): SimpleExoPlayer {
//        return exoPlayer
//    }

    fun addListener(listener: Player.EventListener) {
        if (exoPlayer == null) {
            initPlayer(context)
        }
        exoPlayer!!.addListener(listener)
    }

//    fun setEpisodeTitle(episodeTitle: String) {
//        this.episodeTitle = episodeTitle
//    }
//
//    fun getEpisodeTitle(): String {
//        return this.episodeTitle
//    }
//
//    fun setArtistTitle(artistTitle: String) {
//        this.artistTitle = artistTitle
//    }
//
//    fun getArtistTitle(): String {
//        return this.artistTitle
//    }

    fun release() {
        exoPlayer?.release()
    }


    override fun onDestroy() {
       kill()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }


    fun loadBitmap() {
        Picasso.get().load(viewModel?.currentEpisode?.imageUrl).into(object :
            com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                artwork = bitmap!!
                paletteSwatch = createPaletteSync(artwork!!).dominantSwatch!!
                playerNotificationManager!!.setColor(paletteSwatch.rgb)
            }
        })


    }

    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()


    inner class MyLocalBinder : Binder() {
        fun getService(): PodcastPlayerService {
            return this@PodcastPlayerService
        }
    }

}


