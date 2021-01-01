package com.grey.kotlinpractice.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import tw.ktrssreader.annotation.OrderType
import tw.ktrssreader.annotation.RssAttribute
import tw.ktrssreader.annotation.RssTag
import java.io.Serializable


object Model {

    data class ResultCount(val resultCount: Int)
    data class Results(val results: ArrayList<Podcast>)

    @Entity
    data class Podcast(
        @PrimaryKey (autoGenerate = true) val uid: Int,
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
        public val releaseDate: String? = null
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
        @PrimaryKey val id: Int,
        @ColumnInfo val title: String,
        @ColumnInfo val url: String,
        @ColumnInfo val description: String,
        @ColumnInfo val duration: String,
        @ColumnInfo val pubDate: String,
        @ColumnInfo val podId: String
    )


}