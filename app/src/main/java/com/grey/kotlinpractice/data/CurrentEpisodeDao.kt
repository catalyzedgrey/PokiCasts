//package com.grey.kotlinpractice.data
//
//import androidx.room.*
//
//@Dao
//interface CurrentEpisodeDao {
//    @Query("SELECT * FROM CurrentEpisode")
//    fun getEpisode(): Model.CurrentEpisode
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert( episode: Model.CurrentEpisode)
//
//    @Update
//    fun updateEpisodeData(episode: Model.CurrentEpisode)
//
//    @Delete
//    fun delete(episode: Model.CurrentEpisode)
//
//    @Query("DELETE FROM CurrentEpisode")
//    fun deleteAll()
//}