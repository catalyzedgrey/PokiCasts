package com.grey.kotlinpractice.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Model.Podcast::class, Model.Episode::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao
//    abstract fun currentEpisodeDao(): CurrentEpisodeDao

    object DatabaseProvider {
        private var database: String = "db_classes"
        private var sInstance: AppDatabase? = null
        lateinit var context: Context

        fun getInstance(): AppDatabase {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    database
                ).fallbackToDestructiveMigration().build()
            }

            return sInstance!!
        }

    }
}