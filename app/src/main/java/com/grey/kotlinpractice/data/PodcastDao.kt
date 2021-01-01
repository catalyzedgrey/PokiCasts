package com.grey.kotlinpractice.data

import androidx.room.*

@Dao
interface PodcastDao {
    @Query("SELECT * FROM podcast")
    fun getAll(): List<Model.Podcast>

    @Query("SELECT * FROM podcast WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Model.Podcast>

    @Query("SELECT * FROM podcast WHERE artistName LIKE :name "+ "LIMIT 1")
    fun findByName(name: String): Model.Podcast

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg podcast: Model.Podcast)

    @Update
    fun updatePodcastData(podcast: Model.Podcast)

    @Delete
    fun delete(podcast: Model.Podcast)
}