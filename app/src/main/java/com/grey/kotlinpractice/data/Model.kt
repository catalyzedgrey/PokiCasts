package com.grey.kotlinpractice.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import tw.ktrssreader.model.item.ITunesItemData


object Model {

    data class ResultCount(val resultCount: Int)
    data class Results(val results: ArrayList<Podcast>)

    @Entity
    data class Podcast(
        @PrimaryKey(autoGenerate = true) val uid: Int,
        @ColumnInfo
        @SerializedName("collectionId")
        val collectionId: String,
        @ColumnInfo val artistName: String,
        @ColumnInfo val feedUrl: String,
        @ColumnInfo
        @SerializedName("artworkUrl600")
        @Expose
        public val artworkUrl600: String? = null,
        @ColumnInfo
        @SerializedName("trackCount")
        @Expose
        public val trackCount: Int? = null,
        @ColumnInfo
        @SerializedName("collectionName")
        @Expose
        public val collectionName: String? = null,
        @ColumnInfo
        @SerializedName("releaseDate")
        @Expose
        public val releaseDate: String? = null,
        @ColumnInfo
        public var isSubscribed: Boolean = false
    )


    @Entity(
        foreignKeys = [
            ForeignKey(
                entity = Model.Podcast::class,
                parentColumns = arrayOf("uid"),
                childColumns = arrayOf("podId"),
                onDelete = ForeignKey.CASCADE
            )
        ]
    )
    data class Episode(

        @ColumnInfo val title: String?,
        @PrimaryKey
        @ColumnInfo val url: String,
        @ColumnInfo val description: String?,
        @ColumnInfo val duration: String?,
        @ColumnInfo val pubDate: String?,
        @ColumnInfo val podId: Int?,
        @ColumnInfo val collectionName: String?,
        @ColumnInfo val imageUrl: String?,
        @ColumnInfo var currentPosition: Long?,
        @ColumnInfo var isPlaying: Boolean?,
        @ColumnInfo var isMarkedPlayed: Boolean?,
    )



//    @Entity
//    data class CurrentEpisode(
//        @ColumnInfo val title: String?,
//        @ColumnInfo val url: String?,
//        @ColumnInfo val description: String?,
//        @ColumnInfo val duration: String?,
//        @ColumnInfo val pubDate: String?,
//        val collectionName: String?,
//        val imageUrl: String?,
//        val currentPosition: Long?,
//    ){
//        @PrimaryKey(autoGenerate = true)
//        var id: Int = 0
//    }

}