package com.grey.kotlinpractice.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.PodcastPlayerService
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.EpisodeAdapter
import com.grey.kotlinpractice.data.Model
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottomsheet_episode_preview.*
import java.lang.Exception


class EpisodeFragment : Fragment(), EpisodeAdapter.PlayButtonClickedListener,
    EpisodeAdapter.EpisodeClickedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EpisodeAdapter

    private var itemList = ArrayList<Model.Episode>()

    //    lateinit var itemList: ArrayList<ITunesItemData>
    private var feedUrl: String = ""
    private var artworkUrl: String = ""
    private var collectionName: String = ""
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var podIcon: ImageView
    private lateinit var subscribedBtn: ImageView

    //private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ScrollView>
    private lateinit var appBarLayout: AppBarLayout

    private lateinit var podPreviewTitle: TextView
    private lateinit var podPreviewCollectionName: TextView
    private lateinit var podPreviewDate: TextView
    private lateinit var podPreviewDuration: TextView
    private lateinit var podPreviewDescription: TextView
    private lateinit var podPreviewImage: ImageView
    private lateinit var playBtn: ImageView
    private lateinit var collapsePreview: ImageView
    private lateinit var accentbg: View

    private var podId = -1

    //    lateinit var topBarGroup: Group
    lateinit var episodeUrl: String

    lateinit var podcastPlayerService: PodcastPlayerService
    var isBound = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episode_list, container, false)
        //return inflater.inflate(R.layout.fragment_episode_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        val fragment = this
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PodcastPlayerService.MyLocalBinder
                podcastPlayerService = binder.getService()
                isBound = true
                adapter = EpisodeAdapter(view.context, itemList, podcastPlayerService)
                adapter.setOnPlayButtonClickedListener(fragment)
                adapter.setOnEpisodeClickedListener(fragment)
                recyclerView.adapter = adapter
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
            }
        }
        val intent = Intent(context, PodcastPlayerService::class.java)
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)

        itemList = ArrayList()
        recyclerView.layoutManager = manager

        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet_preview)
        val bottomSheetBehaviorCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        collapseBottomSheet()
                    }
                }
            }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetBehaviorCallback)


        subscribedBtn.setOnClickListener {
            viewModel.unsubscribe(feedUrl)
            this.parentFragmentManager.popBackStack()

        }

        //observeRemoteEpisodeList()
        observeLocalEpisodeList()
    }

    fun collapseBottomSheet() {
        val m = activity as MainActivity
        m.expandBottomNavigationView()
//        topBarGroup.visibility = View.VISIBLE
        appBarLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        viewModel.isEpisodePreviewExpanded = false
    }

    fun updateRecyclerView() {
        try {
            adapter?.notifyDataSetChanged()
        }catch (e: Exception){

        }

    }

    fun expandBottomSheet() {
        val m = activity as MainActivity
        m.collapseBottomNavigationView()
//        topBarGroup.visibility = View.GONE
        appBarLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        viewModel.isEpisodePreviewExpanded = true
    }


    fun initBottomSheetEpisodePreview(view: View) {
        podPreviewTitle = view.findViewById(R.id.episode_title_sheet_preview)
        podPreviewCollectionName = view.findViewById(R.id.artist_title_sheet)
        podPreviewDate = view.findViewById(R.id.sheet_date)
        podPreviewDuration = view.findViewById(R.id.duration)
        podPreviewDescription = view.findViewById(R.id.description)
        podPreviewImage = view.findViewById(R.id.bottomsheet_exoplayer_pod_icon)
        collapsePreview = view.findViewById(R.id.collapse_preview)
        collapsePreview.setOnClickListener {
            collapseBottomSheet()
        }
    }

    fun initViews(view: View) {
        initBottomSheetEpisodePreview(view)
        playBtn = view.findViewById(R.id.play_btn)
        podIcon = view.findViewById(R.id.exoplayer_collapsed_pod_icon)
        subscribedBtn = view.findViewById(R.id.subscribed_btn)
        recyclerView = view.findViewById(R.id.recyclerView)
        accentbg = view.findViewById(R.id.accent_toolbar)

        appBarLayout = view.findViewById(R.id.app_bar_head)

        if (feedUrl != "") {
//            Picasso.get().load(artworkUrl).resize(450, 450)
//                .into(podIcon, Callback() {
//
//                })
            //adapter.artistName = collectionName

            Picasso.get()
                .load(artworkUrl)
                .resize(450, 450)
                .into(podIcon, object : Callback {
                    override fun onSuccess() {
                        createPaletteAsync(podIcon.drawable.toBitmap())
                    }

                    override fun onError(e: Exception?) {}
                })
        }





    }

    fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->

            val lightMutedSwatch = palette!!.lightMutedSwatch
            val lightVibrantSwatch = palette.lightVibrantSwatch
            val darkMutedSwatch = palette.darkMutedSwatch
            val darkVibrantSwatch = palette.darkVibrantSwatch
            val mutedSwatch = palette.mutedSwatch
            val vibrantSwatch = palette.vibrantSwatch
            accentbg.setBackgroundColor(
                darkVibrantSwatch?.rgb ?: darkMutedSwatch?.rgb ?: mutedSwatch?.rgb
                ?: lightMutedSwatch?.rgb ?: vibrantSwatch?.rgb ?: lightVibrantSwatch?.rgb
                ?: palette.dominantSwatch!!.rgb
            )

            playBtn.setColorFilter(
                darkVibrantSwatch?.rgb ?: darkMutedSwatch?.rgb ?: mutedSwatch?.rgb
                ?: lightMutedSwatch?.rgb ?: vibrantSwatch?.rgb ?: lightVibrantSwatch?.rgb
                ?: palette.dominantSwatch!!.rgb
            )
        }
    }

    private fun observeLocalEpisodeList() {
        val resultObserver = Observer<List<Model.Episode>> { result ->
            // Update the UI
            if (result != null) {
                adapter.updateList(result as ArrayList<Model.Episode>)
                itemList = result
            }
        }
        viewModel.getLocalEpisodeList(podId).observe(viewLifecycleOwner, resultObserver)
    }


//    private fun observeRemoteEpisodeList() {
//        val resultObserver = Observer<ITunesChannelData> { result ->
//            // Update the UI
//            itemList = result.items as ArrayList<ITunesItemData>
//            adapter.updateList(itemList)
//
//        }
//        viewModel.getRemoteEpisodeList(feedUrl).observe(viewLifecycleOwner, resultObserver)
//    }


    fun updatePodcastIndex(podId: Int, feedUrl: String, artworkUrl: String, collectionName: String) {
        this.podId = podId
        this.feedUrl = feedUrl
        this.artworkUrl = artworkUrl
        this.collectionName = collectionName
    }

    override fun sendPodcastUri(uri: String) {

        podcastPlayerService.preparePlayer(uri, viewModel.currentEpisode!!.currentPosition!!)
    }



    override fun sendPodcastEpisodeInfo(episode: Model.Episode) {
        if (podcastPlayerService.isPlaying() && podcastPlayerService.currentUri == episode.url) {
            episode.isPlaying = true
            viewModel.currentEpisode = episode
            viewModel.updateEpisode()
        }
        podPreviewTitle.text = episode.title
        podPreviewCollectionName.text = episode.collectionName
        podPreviewDate.text = episode.pubDate
        podPreviewDuration.text = episode.duration
        episodeUrl = episode.url!!

        playBtn.setOnClickListener {
            viewModel.currentEpisode = episode
            podcastPlayerService.preparePlayer(
                episodeUrl,
                viewModel.currentEpisode!!.currentPosition!!
            )
            collapseBottomSheet()
        }

        Picasso.get().load(artworkUrl).resize(450, 450)
            .into(podPreviewImage)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            podPreviewDescription.text = Html.fromHtml(
                episode.description, Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
            )
        } else
            podPreviewDescription.text = Html.fromHtml(episode.description)

        podPreviewDescription.movementMethod = LinkMovementMethod.getInstance();




        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            collapseBottomSheet()
        } else {
            expandBottomSheet()

        }
    }


}