package com.grey.kotlinpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.grey.kotlinpractice.PodcastPlayer
import com.grey.kotlinpractice.R
//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
import com.grey.kotlinpractice.databinding.ActivityMainBinding
//import com.grey.kotlinpractice.di.component.ContextComponent
//import com.grey.kotlinpractice.di.component.DaggerContextComponent
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(), HomeFragment.ItemClickedListener, Player.EventListener,
    TimeBar.OnScrubListener {

    lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val settingsFragment = SettingsFragment()
    private val episodeFragment = EpisodeFragment()
    //private lateinit var exoPlayer: ExoPlayer


    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initPodcastPlayer()


        //region frag
        if (savedInstanceState == null) {
            //val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, homeFragment, homeFragment.javaClass.getSimpleName())
                .commit()
        }


        val bottomNavigationView = view.bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    //val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            homeFragment,
                            homeFragment.javaClass.getSimpleName()
                        )
                        .commit()
                    true
                }
                R.id.menu_search -> {
                    //val fragment = SearchFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            searchFragment,
                            searchFragment.javaClass.getSimpleName()
                        )
                        .commit()
                    true
                }
                R.id.menu_settings -> {
                    //val fragment = SettingsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            settingsFragment,
                            settingsFragment.javaClass.getSimpleName()
                        )
                        .commit()
                    true
                }
                else -> false
            }

            //endregion

        }

        //initUi();
    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is HomeFragment) {
            fragment.setOnItemClickedListener(this)
        }
    }

    override fun sendPodcastIndex(podcastPosIndex: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, episodeFragment, episodeFragment.javaClass.getSimpleName())
            .addToBackStack("tag")
            .commit()
        episodeFragment.updatePodcastIndex(podcastPosIndex)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, playbackState)

    }

    fun initUi() {
//        val searchBtn = findViewById<Button>(R.id.search_btn);
//        searchBtn.setOnClickListener {
//            beginSearch("waypoint");
//        }
    }

    override fun onScrubStart(timeBar: TimeBar, position: Long) {
        //TODO("Not yet implemented")
        timeBar.setEnabled(false)
        return
    }

    override fun onScrubMove(timeBar: TimeBar, position: Long) {
        //TODO("Not yet implemented")
        timeBar.setEnabled(false)
        return
    }

    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
        //TODO("Not yet implemented")
        timeBar.setEnabled(false)
        return
    }

//    fun preparePlayer(uri: String) {
//        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(uri))
//        // Set the media item to be played.
//        exoPlayer.setMediaItem(mediaItem)
//        // Prepare the player.
//        exoPlayer.prepare()
//        // Start the playback.
//        exoPlayer.play()
//
//    }

    private fun initPodcastPlayer() {
        PodcastPlayer.initPlayer(applicationContext)
        val playerView: StyledPlayerControlView =
            findViewById<StyledPlayerControlView>(R.id.exoplayer)
        playerView.player = PodcastPlayer.getPlayer()
        var timeBar: DefaultTimeBar = binding.root.findViewById<DefaultTimeBar>(R.id.exo_progress)
        timeBar.addListener(this)
        timeBar.setEnabled(false)

    }


}




