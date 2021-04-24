package com.grey.kotlinpractice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.exoplayer2.Player
import com.grey.kotlinpractice.HomeViewModel
import com.grey.kotlinpractice.R

class SettingsFragment : Fragment(), Player.EventListener {

    lateinit var skipSilenceSwitch: SwitchCompat
    lateinit var sortSwitch: SwitchCompat

    internal lateinit var callback: OnSwitchToggled

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipSilenceSwitch = view.findViewById(R.id.skip_silence_switch)
        sortSwitch = view.findViewById(R.id.sort_switch)

        skipSilenceSwitch.isChecked = viewModel.isSkippingSilence
        sortSwitch.isChecked = viewModel.isSortingDesc

        skipSilenceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSkippingSilence = isChecked
            callback.onSkipSilenceSwitchChanged(isChecked)
        }

        sortSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSortingDesc = isChecked
//            callback.onSortSwitchChanged(isChecked)

        }
    }

    fun setOnSwitchToggledListener(callback: SettingsFragment.OnSwitchToggled) {
        this.callback = callback
    }

    //
    public interface OnSwitchToggled {
        fun onSkipSilenceSwitchChanged(isChecked: Boolean)
    }

}