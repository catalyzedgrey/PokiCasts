package com.grey.kotlinpractice.ui

//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
//import com.grey.kotlinpractice.di.component.ContextComponent
//import com.grey.kotlinpractice.di.component.DaggerContextComponent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.PodcastPlayerService
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.AppDatabase
import com.grey.kotlinpractice.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.bottomsheet_player.*


class MainActivity : AppCompatActivity(), HomeFragment.ItemClickedListener, Player.EventListener,
    TimeBar.OnScrubListener {

    lateinit var binding: ActivityMainBinding
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val settingsFragment = SettingsFragment()
    private val episodeFragment = EpisodeFragment()

    lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var imgViewSheet: ImageView
    lateinit var defaultTimeBar: DefaultTimeBar
    lateinit var playerView: StyledPlayerControlView
    lateinit var bottomPlayerView: StyledPlayerControlView

    lateinit var podcastPlayerService: PodcastPlayerService
    var isBound = false
    lateinit var bottomPodcastName: TextView
    lateinit var bottomArtistName: TextView
    lateinit var connection: ServiceConnection

    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (savedInstanceState == null) {
            //val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, homeFragment, homeFragment.javaClass.simpleName)
                .commit()
        }

        val activity = this
        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PodcastPlayerService.MyLocalBinder
                podcastPlayerService = binder.getService()
                playerView.player = podcastPlayerService.exoPlayer
                bottomPlayerView.player = podcastPlayerService.exoPlayer
                isBound = true
                podcastPlayerService.addListener(activity)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }

        val intent = Intent(this, PodcastPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        initUi()
        handleBottomNavSwitching()

    }


    private fun handleBottomNavSwitching() {
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
        }
    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is HomeFragment) {
            fragment.setOnItemClickedListener(this)
        }
    }

    override fun sendPodcastIndex(
        podcastPosIndex: String,
        artworkUrl: String,
        collectionName: String
    ) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, episodeFragment, episodeFragment.javaClass.getSimpleName())
            .addToBackStack("tag")
            .commit()
        podcastPlayerService.artworkUrl = artworkUrl
        podcastPlayerService.loadBitmap()
        episodeFragment.updatePodcastIndex(podcastPosIndex, artworkUrl, collectionName)
    }


    fun collapseBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expandBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun collapseBottomNavigationView() {
        bottomNavigationView.visibility = View.GONE
        playerView.visibility = View.GONE
    }

    fun expandBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
        playerView.visibility = View.VISIBLE
    }

    private fun initUi() {
        AppDatabase.DatabaseProvider.context = applicationContext
        bottomNavigationView = binding.root.findViewById(R.id.bottomNavigationView)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        imgViewSheet = binding.root.findViewById<ImageView>(R.id.mainpodIconControl)
        playerView = binding.root.findViewById<StyledPlayerControlView>(R.id.exoplayer)
        bottomPlayerView = binding.root.findViewById<StyledPlayerControlView>(R.id.player_control)

        bottomPodcastName = binding.root.findViewById(R.id.episode_title_sheet_preview)
        bottomArtistName = binding.root.findViewById(R.id.artist_title_sheet)
        defaultTimeBar = binding.root.findViewById<DefaultTimeBar>(R.id.exo_progress)
        defaultTimeBar.addListener(this)

        val bottomSheetBehaviorCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        expandBottomNavigationView()

                    }
                }
            }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetBehaviorCallback)
        imgViewSheet.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                collapseBottomSheet()
            } else {
                expandBottomSheet()
                collapseBottomNavigationView()

            }
        }
    }


    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else if (viewModel.isEpisodePreviewExpanded) {
            episodeFragment.collapseBottomSheet()
        } else
            super.onBackPressed()

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (isPlaying) {
            val epTitle = podcastPlayerService.episodeTitle //.getEpisodeTitle()
            val artistName = podcastPlayerService.artistTitle //getArtistTitle()
            bottomPodcastName.text = epTitle
            bottomArtistName.text = artistName
        }
    }

    override fun onStop() {
        super.onStop()

        //PodcastPlayerService.release()
        //PodcastPlayerService.onDestroy()

    }

    override fun onDestroy() {
        podcastPlayerService.release()
        podcastPlayerService.stopSelf()
        podcastPlayerService.onDestroy()
        super.onDestroy()
    }

    //region trashy scrub handling
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
    //endregion
}




