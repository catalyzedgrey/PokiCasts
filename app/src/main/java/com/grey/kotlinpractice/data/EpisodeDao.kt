package com.grey.kotlinpractice.data

import androidx.room.*
import tw.ktrssreader.model.item.ITunesItemData

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episode")
    fun getAll(): List<Model.Episode>

    @Query("SELECT * FROM episode WHERE podId Like :podId")
    fun getAllByPodId(podId: Int): List<Model.Episode>

//    @Query("SELECT * FROM episode WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Model.Podcast>

    @Query("SELECT * FROM episode WHERE url LIKE :url")
    fun getEpisodeByUrl(url: String): Model.Episode

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

    @Query("SELECT * FROM episode WHERE isPlaying LIKE 1")
    fun getCurrentPlayingEpisode(): Model.Episode

    @Query("SELECT * FROM episode WHERE isMarkedPlayed LIKE 1")
    fun getPlayedEpisode(): Model.Episode

    fun transformItunesDatatoEpisode(itunesItemData:  List<ITunesItemData>, podId: Int, podName: String, artworkUrl: String): List<Model.Episode>{
        val episodes: ArrayList<Model.Episode> = ArrayList()
        for(item in itunesItemData){
            val e = Model.Episode(
                title = item.title,
                url = item.enclosure!!.url!!,
                description = item.description,
                duration = item.duration,
                pubDate = item.pubDate,
                podId = podId,
                collectionName = podName,
                imageUrl = artworkUrl,
                currentPosition = 0,
                isPlaying = false,
                isMarkedPlayed = false
            )
            episodes.add(e)

        }


        return episodes
    }

//    @Query("SELECT * FROM episode WHERE feedUrl IN (:feedUrl)")
//    fun findByFeedUrl(feedUrl: String): Model.Podcast
}