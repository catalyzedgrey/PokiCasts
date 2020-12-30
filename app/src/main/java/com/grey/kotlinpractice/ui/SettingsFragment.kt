package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.grey.kotlinpractice.PodcastPlayer
import com.grey.kotlinpractice.R

class SettingsFragment : Fragment(), Player.EventListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var playerView: StyledPlayerControlView = view.findViewById<StyledPlayerControlView>(R.id.player_control)

        playerView.player= PodcastPlayer.getPlayer()
        PodcastPlayer.addListener(this)
        view.findViewById<ImageView>(R.id.pod_icon).setImageResource(R.drawable.waypoint)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
    }
}