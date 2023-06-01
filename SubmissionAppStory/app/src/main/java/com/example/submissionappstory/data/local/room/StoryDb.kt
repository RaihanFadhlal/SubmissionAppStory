package com.example.submissionappstory.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.submissionappstory.data.local.pagedir.DaoDirection
import com.example.submissionappstory.data.local.pagedir.EntityDirection
import com.example.submissionappstory.data.remote.apiresponse.ListStory

@Database(
    entities = [ListStory::class, EntityDirection::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDb : RoomDatabase() {
    abstract fun storyDao() : StoryDao
    abstract fun remoteKeysDao() : DaoDirection

    companion object {
        @Volatile
        private var INSTANCE: StoryDb? = null

        @JvmStatic
        fun getInstance(context: Context): StoryDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDb::class.java, "stories_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}