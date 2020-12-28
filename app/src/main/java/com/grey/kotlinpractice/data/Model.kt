package com.grey.kotlinpractice.data

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import tw.ktrssreader.annotation.RssTag
import java.io.Serializable


object Model {

    data class ResultCount(val resultCount: Int)
    data class Results(val results: ArrayList<Podcast>)

    data class Podcast(val artistName: String, val feedUrl: String) {

        @SerializedName("artworkUrl600")
        @Expose
        public val artworkUrl600: String? = null

        @SerializedName("trackCount")
        @Expose
        public val trackCount: Int? = null

        @SerializedName("collectionName")
        @Expose
        public val collectionName: String? = null

    }

//    @RssTag(name = "channel")
//    data class Ep(val title: String, val url: String, val description: String, val duration: String, val pubDate: String ):Serializable{}
}