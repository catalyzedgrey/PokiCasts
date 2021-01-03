package com.grey.kotlinpractice.ui

//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
//import com.grey.kotlinpractice.di.component.ContextComponent
//import com.grey.kotlinpractice.di.component.DaggerContextComponent
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.offline.DownloadService.startForeground
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.PodcastPlayer
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


    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            //val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, homeFragment, homeFragment.javaClass.getSimpleName())
                .commit()
        }

        initUi()
        initPodcastPlayer()
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
        PodcastPlayer.artworkUrl = artworkUrl
        PodcastPlayer.loadBitmap()
        episodeFragment.updatePodcastIndex(podcastPosIndex, artworkUrl, collectionName)
    }


    fun collapseBottomSheet(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expandBottomSheet(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun collapseBottomNavigationView(){
        bottomNavigationView.visibility = View.GONE
        playerView.visibility = View.GONE
    }

    fun expandBottomNavigationView(){
        bottomNavigationView.visibility = View.VISIBLE
        playerView.visibility = View.VISIBLE
    }

    private fun initUi() {
        AppDatabase.DatabaseProvider.context = applicationContext
        bottomNavigationView = binding.root.findViewById(R.id.bottomNavigationView)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        imgViewSheet = binding.root.findViewById<ImageView>(R.id.mainpodIconControl)

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

    private fun initPodcastPlayer() {
        //val intent = Intent(this, PodcastPlayer::class.java)

        //startService(intent)
        PodcastPlayer.initPlayer(applicationContext)
        playerView = binding.root.findViewById<StyledPlayerControlView>(R.id.exoplayer)
        playerView.player = PodcastPlayer.getPlayer()
        PodcastPlayer.addListener(this)


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
            val epTitle = PodcastPlayer.getEpisodeTitle()
            val artName = PodcastPlayer.getArtistTitle()
            findViewById<TextView>(R.id.episode_title_sheet_preview).text = epTitle
            findViewById<TextView>(R.id.artist_title_sheet).text = artName

//            val pendingIntent: PendingIntent =
//                Intent(this, PodcastPlayer::class.java).let { notificationIntent ->
//                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
//                }
//
//            val notification: Notification = NotificationCompat.Builder(this, )
//                .setContentTitle(getText(R.string.notification_title))
//                .setContentText(getText(R.string.notification_message))
//                .setSmallIcon(R.drawable.icon)
//                .setContentIntent(pendingIntent)
//                .setTicker(getText(R.string.ticker_text))
//                .build()
//
//// Notification ID cannot be 0.
//            startForeground(ONGOING_NOTIFICATION_ID, notification)


        }
    }

    override fun onStop() {
        PodcastPlayer.release()
        PodcastPlayer.onDestroy()
        super.onStop()
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




