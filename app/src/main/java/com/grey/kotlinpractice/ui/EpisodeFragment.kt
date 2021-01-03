package com.grey.kotlinpractice.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
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
import com.grey.kotlinpractice.PodcastPlayer
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

    //    lateinit var topBarGroup: Group
    lateinit var episodeUrl: String


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

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        itemList = ArrayList()
        recyclerView.layoutManager = manager
        adapter = EpisodeAdapter(view.context, itemList)
        adapter.setOnPlayButtonClickedListener(this)
        adapter.setOnEpisodeClickedListener(this)
        recyclerView.adapter = adapter



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

    fun expandBottomSheet() {
        val m = activity as MainActivity
        m.collapseBottomNavigationView()
//        topBarGroup.visibility = View.GONE
        appBarLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        viewModel.isEpisodePreviewExpanded = true
    }


    fun initViews(view: View) {
        podPreviewTitle = view.findViewById(R.id.episode_title_sheet_preview)
        podPreviewCollectionName = view.findViewById(R.id.artist_title_sheet)
        podPreviewDate = view.findViewById(R.id.sheet_date)
        podPreviewDuration = view.findViewById(R.id.duration)
        podPreviewDescription = view.findViewById(R.id.description)
        podPreviewImage = view.findViewById(R.id.pod_icon)
        playBtn = view.findViewById(R.id.play_btn)
        podIcon = view.findViewById(R.id.mainpodIconControl)
        subscribedBtn = view.findViewById(R.id.subscribed_btn)
        recyclerView = view.findViewById(R.id.recyclerView)
        accentbg = view.findViewById(R.id.accent_toolbar)

//        topBarGroup = view.findViewById(R.id.group)
        collapsePreview = view.findViewById(R.id.collapse_preview)
        collapsePreview.setOnClickListener {
            collapseBottomSheet()
        }
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



        playBtn.setOnClickListener {
            PodcastPlayer.preparePlayer(episodeUrl)
            collapseBottomSheet()
        }

    }

    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()
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
                ?: lightMutedSwatch?.rgb ?: lightVibrantSwatch?.rgb ?: vibrantSwatch?.rgb?: palette.dominantSwatch!!.rgb
            )



            playBtn.setColorFilter(
                darkVibrantSwatch?.rgb ?: darkMutedSwatch?.rgb ?: mutedSwatch?.rgb
                ?: lightMutedSwatch?.rgb ?: lightVibrantSwatch?.rgb ?: vibrantSwatch?.rgb?: palette.dominantSwatch!!.rgb
            )

            //region trying out different swatches
//            var x=4
//            when (x){
//                0 -> appBarLayout.background.setTint(palette!!.lightVibrantSwatch!!.rgb)
//                1 ->appBarLayout.background.setTint(palette!!.vibrantSwatch!!.rgb)
//                2 ->appBarLayout.background.setTint(palette!!.darkVibrantSwatch!!.rgb)
//                3 ->appBarLayout.background.setTint(palette!!.lightMutedSwatch!!.rgb)
//                4->appBarLayout.background.setTint(palette!!.mutedSwatch!!.rgb)
//                5 ->appBarLayout.background.setTint(palette!!.darkMutedSwatch!!.rgb)
//            }
//            if (palette!!.lightVibrantSwatch != null)
//                appBarLayout.background.setTint(palette.lightVibrantSwatch!!.rgb)
//            else if (palette!!.vibrantSwatch != null)
//                appBarLayout.background.setTint(palette.vibrantSwatch!!.rgb)
//            else if (palette!!.darkVibrantSwatch != null)
//                appBarLayout.background.setTint(palette.darkVibrantSwatch!!.rgb)
//            else if (palette!!.lightMutedSwatch != null)
//                appBarLayout.background.setTint(palette.lightMutedSwatch!!.rgb)
//            else if (palette!!.mutedSwatch != null)
//                appBarLayout.background.setTint(palette.mutedSwatch!!.rgb)
//            else if (palette!!.darkMutedSwatch != null)
//                appBarLayout.background.setTint(palette.darkMutedSwatch!!.rgb)
//            else
//                appBarLayout.background.setTint(palette.dominantSwatch!!.rgb)
            //endregion
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
        viewModel.getLocalEpisodeList(feedUrl).observe(viewLifecycleOwner, resultObserver)
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


    fun updatePodcastIndex(feedUrl: String, artworkUrl: String, collectionName: String) {
        this.feedUrl = feedUrl
        this.artworkUrl = artworkUrl
        this.collectionName = collectionName
    }

    override fun sendPodcastUri(uri: String) {
        PodcastPlayer.preparePlayer(uri)
    }


    override fun sendPodcastEpisodeInfo(episode: Model.Episode) {
        podPreviewTitle.text = episode.title
        podPreviewCollectionName.text = episode.collectionName
        podPreviewDate.text = episode.pubDate
        podPreviewDuration.text = episode.duration
        episodeUrl = episode.url!!

        Picasso.get().load(artworkUrl).resize(450, 450)
            .into(podPreviewImage)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            podPreviewDescription.text = Html.fromHtml(
                episode.description, Html.FROM_HTML_MODE_COMPACT
            )
        } else
            podPreviewDescription.text = Html.fromHtml(episode.description)



        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            collapseBottomSheet()
        } else {
            expandBottomSheet()

        }
    }


}