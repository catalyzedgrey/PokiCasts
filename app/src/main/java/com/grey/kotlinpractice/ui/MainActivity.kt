package com.grey.kotlinpractice.ui

//import com.grey.kotlinpractice.adapter.PodcastHomeAdapter
//import com.grey.kotlinpractice.di.component.ContextComponent
//import com.grey.kotlinpractice.di.component.DaggerContextComponent
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.PodcastPlayerService
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.data.AppDatabase
import com.grey.kotlinpractice.data.Model
import com.grey.kotlinpractice.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottomsheet_episode_description.*
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
    private lateinit var bottomSheetEpisodeDescriptionBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var exoplayerCollapsedPodIcon: ImageView
    lateinit var defaultTimeBar: DefaultTimeBar
    lateinit var playerView: StyledPlayerControlView
    lateinit var bottomPlayerView: StyledPlayerControlView

    lateinit var podcastPlayerService: PodcastPlayerService
    var isBound = false

    lateinit var bottomSheetPodcastName: TextView
    lateinit var bottomSheetPlayerArtistName: TextView
    lateinit var bottomSheetPlayerPodIcon: ImageView
    lateinit var speedControlButton: ImageView

    lateinit var connection: ServiceConnection

    lateinit var sharedpreferences: SharedPreferences
    lateinit var speedLabel: TextView
    lateinit var speedGroup: Group
    lateinit var minusSpeed: ImageView
    lateinit var plusSpeed: ImageView
    lateinit var bottomControlDescription: ImageView
    lateinit var bottomSheetDialogFragment: BottomSheetEpisodeDescriptionFragment


    private val viewModel: HomeViewModel by viewModels()


    fun updatePlayerUI() {
//        val resultObserver = Observer<Model.CurrentEpisode> { result ->
//            // Update the UI
//            if (result != null) {
//                bottomSheetPodcastName.text = result.title
//                bottomSheetPlayerArtistName.text = result.collectionName
//                Picasso.get().load(result.imageUrl).into(exoplayerCollapsedPodIcon)
//                Picasso.get().load(result.imageUrl).into(bottomSheetPlayerPodIcon)
//                viewModel.currentEpisode = convertCurrentEpisodeToEpisode(result)
//            }
//        }
//        viewModel.getLastPlayerEpisode().observe(this, resultObserver)


        val urlKey = sharedpreferences.getString("urlKey", "")

        val resultObserver = Observer<Model.Episode> { result ->
            // Update the UI
            if (result != null) {
                bottomSheetPodcastName.text = result.title
                bottomSheetPlayerArtistName.text = result.collectionName
                Picasso.get().load(result.imageUrl).into(exoplayerCollapsedPodIcon)
                Picasso.get().load(result.imageUrl).into(bottomSheetPlayerPodIcon)
                viewModel.currentEpisode = result

            }
        }

        viewModel.getEpisodeByFeed(urlKey!!).observe(this, resultObserver)
    }


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

        sharedpreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val activity = this
        connection =
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val binder = service as PodcastPlayerService.MyLocalBinder
                    podcastPlayerService = binder.getService()
                    playerView.player = podcastPlayerService.exoPlayer
                    bottomPlayerView.player = podcastPlayerService.exoPlayer
                    isBound = true
                    podcastPlayerService.addListener(activity)
                    podcastPlayerService.viewModel = viewModel
                    if (viewModel.currentEpisode != null) {
                        podcastPlayerService.preparePlayer(
                            viewModel.currentEpisode!!.url,
                            viewModel.currentEpisode!!.currentPosition!!
                        )
                        podcastPlayerService.pause()
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    isBound = false
                }
            }

        val intent = Intent(this, PodcastPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        initUi()
        handleBottomNavSwitching()
        handleSpeed()
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
        //podcastPlayerService.artworkUrl = artworkUrl
        //podcastPlayerService.loadBitmap()
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


    private fun updateUI(){
        //the expanded player part
        bottomSheetPodcastName.text = viewModel.currentEpisode?.title
        bottomSheetPlayerArtistName.text = viewModel.currentEpisode?.collectionName
        Picasso.get().load(viewModel.currentEpisode?.imageUrl).into(exoplayerCollapsedPodIcon)
        Picasso.get().load(viewModel.currentEpisode?.imageUrl).into(bottomSheetPlayerPodIcon)

        //description bottomsheet part


    }

    private fun initBottomSheetDescriptionPage(){
        bottom_sheet_ep_description_title
        bottom_sheet_ep_description_release
        bottom_sheet_ep_description_duration
        bottom_sheet_ep_description_text


    }
    private fun initUi() {

        AppDatabase.DatabaseProvider.context = applicationContext
        bottomNavigationView = binding.root.findViewById(R.id.bottomNavigationView)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetEpisodeDescriptionBehavior = BottomSheetBehavior.from(
//            episode_description_bottomsheet
//        )

        exoplayerCollapsedPodIcon =
            binding.root.findViewById<ImageView>(R.id.exoplayer_collapsed_pod_icon)
        playerView = binding.root.findViewById<StyledPlayerControlView>(R.id.exoplayer)
        bottomPlayerView = binding.root.findViewById<StyledPlayerControlView>(R.id.player_control)

        bottomSheetPodcastName = binding.root.findViewById(R.id.episode_title_sheet_preview)
        bottomSheetPlayerArtistName = binding.root.findViewById(R.id.artist_title_sheet)
        bottomSheetPlayerPodIcon = binding.root.findViewById(R.id.bottomsheet_exoplayer_pod_icon)

        speedControlButton = binding.root.findViewById(R.id.speed_control)
        speedGroup = binding.root.findViewById(R.id.speed_group)
        plusSpeed = binding.root.findViewById(R.id.plus_speed)
        minusSpeed = binding.root.findViewById(R.id.minus_speed)
        speedLabel = binding.root.findViewById(R.id.speed_text_view)

        bottomControlDescription = binding.root.findViewById(R.id.bottom_description)

        bottomControlDescription.setOnClickListener{
            bottomSheetDialogFragment = BottomSheetEpisodeDescriptionFragment()
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
            updateEpisodeDescriptionBottomSheet()
//            bottomSheetEpisodeDescriptionBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }


        speedControlButton.setOnClickListener {
            if (speedGroup.visibility == View.VISIBLE) {
                speedGroup.visibility = View.GONE
            } else
                speedGroup.visibility = View.VISIBLE
        }



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
        exoplayerCollapsedPodIcon.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                collapseBottomSheet()
            } else {
                expandBottomSheet()
                collapseBottomNavigationView()

            }
        }
        val i = intent
        val shouldExpandSheet = i.getBooleanExtra("shouldExpandSheet", false)
        val episodeTitle = i.getStringExtra("episodeTitle")
        val artistTitle = i.getStringExtra("artistTitle")

        updatePlayerUI()
        if (shouldExpandSheet) {
            expandBottomSheet()
            collapseBottomNavigationView()
        }

    }

    private fun updateEpisodeDescriptionBottomSheet() {


        bottomSheetDialogFragment.updateUI(viewModel.currentEpisode?.title!!, viewModel.currentEpisode?.pubDate!!,viewModel.currentEpisode?.duration!!,viewModel.currentEpisode?.description!! )
    }


    private fun handleSpeed() {
        var speed: Float = 1f
        plusSpeed.setOnClickListener {
            if (speed <= 3) {
                speed += 0.1f
                var speedplus = String.format("%.2f", speed)
                podcastPlayerService.changePlaybackSpeed(speedplus.toFloat())
                if (speedplus != "1.0")
                    speedplus = speedplus.substring(0, speedplus.length - 1)

                speedLabel.text = speedplus + "x"
            }
        }
        minusSpeed.setOnClickListener {
            if (speed >= 0.5) {
                speed -= 0.1f
                var speedplus = String.format("%.2f", speed)
                podcastPlayerService.changePlaybackSpeed(speedplus.toFloat())
                if (speedplus != "1.0")
                    speedplus = speedplus.substring(0, speedplus.length - 1)
                speedLabel.text = speedplus + "x"
            }
        }

    }

    override fun onBackPressed() {
        if (speedGroup.visibility == View.VISIBLE)
            speedGroup.visibility = View.GONE
        //.state = BottomSheetBehavior.STATE_COLLAPSED

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
            //val epTitle = podcastPlayerService.episodeTitle //.getEpisodeTitle()
            //val artistName = podcastPlayerService.artistTitle //getArtistTitle()

            updateUI()
//            bottomSheetPodcastName.text = viewModel.currentEpisode?.title
//            bottomSheetPlayerArtistName.text = viewModel.currentEpisode?.collectionName
//            Picasso.get().load(viewModel.currentEpisode?.imageUrl).into(exoplayerCollapsedPodIcon)
//            Picasso.get().load(viewModel.currentEpisode?.imageUrl).into(bottomSheetPlayerPodIcon)

        }
    }

    override fun onStop() {
        super.onStop()
        if (viewModel.currentEpisode != null) {
            //val currentEpisode: Model.CurrentEpisode = Util.convertEpisodeToCurrentEpisode(viewModel.currentEpisode!!, podcastPlayerService.exoPlayer!!.currentPosition)
            //viewModel.saveLastPlayedPodcastInfo(currentEpisode)


            viewModel.currentEpisode!!.currentPosition =
                podcastPlayerService.exoPlayer!!.currentPosition
            viewModel.saveLastPlayedPodcastInfo(viewModel.currentEpisode!!)
            val editor: SharedPreferences.Editor = sharedpreferences.edit()
            editor.putString("urlKey", viewModel.currentEpisode!!.url!!)
            editor.commit()


        }

        //PodcastPlayerService.release()
        //PodcastPlayerService.onDestroy()

    }


    override fun onDestroy() {
        podcastPlayerService.release()
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




