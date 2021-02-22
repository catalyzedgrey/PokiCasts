package com.grey.kotlinpractice.ui


import android.annotation.SuppressLint
import android.app.Dialog
import android.text.Html
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.grey.kotlinpractice.R


class BottomSheetEpisodeDescriptionFragment : BottomSheetDialogFragment() {

    lateinit var titletv: TextView
    lateinit var releasedatetv: TextView
    lateinit var durationtv: TextView
    lateinit var descriptiontv: TextView
    lateinit var eptitle: String
    lateinit var title: String
    lateinit var  releaseDate: String
    lateinit var duration: String
    lateinit var description: String


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView =
            View.inflate(context, R.layout.bottomsheet_episode_description, null)

        titletv = contentView.findViewById(R.id.bottom_sheet_ep_description_title)
        titletv.text = eptitle
        releasedatetv = contentView.findViewById(R.id.bottom_sheet_ep_description_release)
        releasedatetv.text = releaseDate
        durationtv = contentView.findViewById(R.id.bottom_sheet_ep_description_duration)
        durationtv.text = duration
        descriptiontv = contentView.findViewById(R.id.bottom_sheet_ep_description_text)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            descriptiontv.text = Html.fromHtml(description, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
            )
        } else
            descriptiontv.text = Html.fromHtml(description)




        dialog.setContentView(contentView)
    }

    fun updateUI(title: String, releaseDate: String, duration: String, description: String) {
        this.eptitle = title
        this.releaseDate = releaseDate
        this.duration = duration
        this.description = description
    }
}