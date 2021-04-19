package com.grey.kotlinpractice.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.grey.kotlinpractice.R

class BottomSheetSpeedControlFragment: BottomSheetDialogFragment() {


    lateinit var speedTextView: TextView
    lateinit var speedLabelTextView: TextView
    lateinit var plusSpeedImageView: ImageView
    lateinit var minusSpeedImageView: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contentView =
            View.inflate(context, R.layout.bottomsheet_speed_control, null)
        speedTextView = contentView.findViewById(R.id.speed_text_view)
        speedLabelTextView = contentView.findViewById(R.id.speed_label)
        plusSpeedImageView = contentView.findViewById(R.id.plus_speed)
        minusSpeedImageView = contentView.findViewById(R.id.minus_speed)

        handleInput()
        return contentView

    }

    private fun handleInput(){
        var speed: Float = 1f
        var mainActivity = activity as MainActivity
        plusSpeedImageView.setOnClickListener{
            if (speed <= 3) {
                speed += 0.1f
                var speedplus = String.format("%.2f", speed)

                //
                // change to main activity handling the speed change
                // podcastPlayerService.changePlaybackSpeed(speedplus.toFloat())
                mainActivity.handleSpeed(speedplus.toFloat())

                if (speedplus != "1.0")
                    speedplus = speedplus.substring(0, speedplus.length - 1)

                speedTextView.text = speedplus + "x"
            }


        }

        minusSpeedImageView.setOnClickListener{
            if (speed >= 0.5) {
                speed -= 0.1f
                var speedplus = String.format("%.2f", speed)
                mainActivity.handleSpeed(speedplus.toFloat())
                if (speedplus != "1.0")
                    speedplus = speedplus.substring(0, speedplus.length - 1)
                speedTextView.text = speedplus + "x"
            }
        }



    }



}