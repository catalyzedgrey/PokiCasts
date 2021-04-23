package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.grey.kotlinpractice.R
import com.grey.kotlinpractice.utils.Util

class SettingsFragment : Fragment(), Player.EventListener {

    lateinit var skipSilenceSwitch: SwitchCompat
    lateinit var sortSwitch: SwitchCompat

    internal lateinit var callback: OnSwitchToggled

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipSilenceSwitch = view.findViewById(R.id.skip_silence_switch)
        sortSwitch = view.findViewById(R.id.sort_switch)

        skipSilenceSwitch.setOnCheckedChangeListener {
                _, isChecked ->

           callback.onSkipSilenceSwitchChanged(isChecked)

        }

        sortSwitch.setOnCheckedChangeListener {
                _, isChecked ->
            callback.onSortSwitchChanged(isChecked)

        }
    }

    fun setOnSwitchToggledListener(callback: SettingsFragment.OnSwitchToggled){
        this.callback = callback
    }

    public interface OnSwitchToggled{
        fun onSortSwitchChanged(isChecked: Boolean)
        fun onSkipSilenceSwitchChanged(isChecked: Boolean)
    }

}