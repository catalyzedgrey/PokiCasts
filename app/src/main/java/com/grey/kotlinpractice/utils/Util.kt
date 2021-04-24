package com.grey.kotlinpractice.utils


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast



@SuppressLint("StaticFieldLeak")
object Util {

    lateinit var context: Context

    val HOME_BIG_ICONS = 3
    val HOME_SMALL_ICONS = 4

    fun stripTimeFromDateString(dateTime: String): String{
        var stringList = dateTime.split(" ")
        return stringList[0]+ " " +stringList[1] + " "+ stringList[2] +" "+ stringList [3]
    }

    fun showToast(message: String ){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

//    fun convertEpisodeToCurrentEpisode(
//        episode: Model.Episode,
//        currentPosition: Long
//    ): Model.CurrentEpisode {
//        return Model.CurrentEpisode(
//            episode.title,
//            episode.url,
//            episode.description,
//            episode.duration,
//            episode.pubDate,
//            episode.collectionName,
//            episode.imageUrl,
//            currentPosition
//        )
//    }
//
//    fun convertCurrentEpisodeToEpisode(currentEpisode: Model.CurrentEpisode): Model.Episode {
//
//        return Model.Episode(
//            currentEpisode.title,
//            currentEpisode.url,
//            currentEpisode.description,
//            currentEpisode.duration,
//            currentEpisode.pubDate,
//            null,
//            currentEpisode.collectionName,
//            currentEpisode.imageUrl,
//        )
//
//    }

}