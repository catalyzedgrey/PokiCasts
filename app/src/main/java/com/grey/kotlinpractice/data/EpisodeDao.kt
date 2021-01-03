package com.grey.kotlinpractice.data

import androidx.room.*
import tw.ktrssreader.model.item.ITunesItem
import tw.ktrssreader.model.item.ITunesItemData

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episode")
    fun getAll(): List<Model.Episode>

//    @Query("SELECT * FROM episode WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Model.Podcast>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg episode: List<ITunesItemData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( episode: List<Model.Episode>)

    @Update
    fun updateEpisodeData(episode: Model.Episode)

    @Delete
    fun delete(episode: Model.Episode)

    @Query("DELETE FROM episode")
    fun deleteAll()

    fun transformItunesDatatoEpisode(itunesItemData:  List<ITunesItemData>, podId: Int, podName: String, artworkUrl: String): List<Model.Episode>{
        val episodes: ArrayList<Model.Episode> = ArrayList()
        for(item in itunesItemData){
            val e = Model.Episode(
                item.title,
                item.enclosure!!.url,
                item.description,
                item.duration,
                item.pubDate,
                podId,
                podName,
                artworkUrl
            )
            episodes.add(e)
        }


        return episodes
    }

//    @Query("SELECT * FROM episode WHERE feedUrl IN (:feedUrl)")
//    fun findByFeedUrl(feedUrl: String): Model.Podcast
}