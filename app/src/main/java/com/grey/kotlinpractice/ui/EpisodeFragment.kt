package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.PodcastPlayer
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.adapter.EpisodeAdapter
import com.grey.kotlinpractice.data.Model
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_episode_preview.*
import kotlinx.android.synthetic.main.bottomsheet_player.*
import tw.ktrssreader.model.channel.ITunesChannelData
import tw.ktrssreader.model.item.ITunesItemData


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

    private lateinit var podTitle: TextView
    private lateinit var podCollectionName: TextView
    private lateinit var podDate: TextView
    private lateinit var podDuration: TextView
    private lateinit var podDescription: TextView
    private lateinit var podImagePreview: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_episode_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout = view.findViewById(R.id.app_bar_head)

        initViews(view)

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        itemList = ArrayList()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = manager
        adapter = EpisodeAdapter(view.context, itemList)
        adapter.setOnPlayButtonClickedListener(this)
        adapter.setOnEpisodeClickedListener(this)
        recyclerView.adapter = adapter
        podIcon = view.findViewById(R.id.mainpodIconControl)
        subscribedBtn = view.findViewById(R.id.subscribed_btn)


        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet_preview)
        val bottomSheetBehaviorCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        appBarLayout.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetBehaviorCallback)


        subscribedBtn.setOnClickListener {
            viewModel.unsubscribe(feedUrl)
            this.parentFragmentManager.popBackStack()

        }

        updateRecyclerViewResults()
        //observeRemoteEpisodeList()
        observeLocalEpisodeList()
    }

    private fun updateRecyclerViewResults() {
        if (feedUrl != "") {
            Picasso.get().load(artworkUrl).resize(450, 450)
                .into(podIcon)
            adapter.artistName = collectionName
        }
    }

    fun initViews(view: View) {
        podTitle = view.findViewById(R.id.episode_title_sheet)
        podCollectionName = view.findViewById(R.id.artist_title_sheet)
        podDate = view.findViewById(R.id.sheet_date)
        podDuration = view.findViewById(R.id.duration)
        podDescription = view.findViewById(R.id.description)
        podImagePreview = view.findViewById(R.id.pod_icon)
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

        podTitle.text = episode.title
        podCollectionName.text = episode.collectionName
        podDate.text = episode.pubDate
        podDuration.text = episode.duration
        Picasso.get().load(artworkUrl).resize(450, 450)
            .into(podImagePreview)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            podDescription.text = Html.fromHtml(episode.description, Html.FROM_HTML_MODE_COMPACT)
        }

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            appBarLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            appBarLayout.visibility = View.GONE
            recyclerView.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
    }


}