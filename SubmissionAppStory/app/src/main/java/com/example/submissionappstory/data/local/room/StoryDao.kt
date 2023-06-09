package com.example.submissionappstory.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionappstory.data.remote.apiresponse.ListStory

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(listStoryItem: ListStory)

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getStory(): PagingSource<Int, ListStory>

    @Query("DELETE FROM stories")
    suspend fun deleteStory()
}