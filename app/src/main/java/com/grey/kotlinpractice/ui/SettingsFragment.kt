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
import com.grey.kotlinpractice.utils.Util

class SettingsFragment : Fragment(), Player.EventListener {

    lateinit var skipSilenceSwitch: SwitchCompat
    lateinit var sortSwitch: SwitchCompat
    lateinit var changeIconSizeSwitch: SwitchCompat

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
        changeIconSizeSwitch = view.findViewById(R.id.compact_switch)

        skipSilenceSwitch.isChecked = viewModel.isSkippingSilence
        sortSwitch.isChecked = viewModel.isSortingDesc

        if(viewModel.homeIconSize == Util.HOME_SMALL_ICONS)
            changeIconSizeSwitch.isChecked = true

        skipSilenceSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSkippingSilence = isChecked
            callback.onSkipSilenceSwitchChanged(isChecked)
        }

        sortSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSortingDesc = isChecked
//            callback.onSortSwitchChanged(isChecked)
        }
        changeIconSizeSwitch.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked)
                viewModel.homeIconSize = Util.HOME_SMALL_ICONS
            else
                viewModel.homeIconSize = Util.HOME_BIG_ICONS

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