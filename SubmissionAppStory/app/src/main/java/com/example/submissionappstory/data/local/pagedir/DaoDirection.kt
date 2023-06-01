package com.example.submissionappstory.data.local.pagedir

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoDirection {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(remoteKeys: List<EntityDirection>)

    @Query("SELECT * FROM entity_direction WHERE id = :id")
    suspend fun getRemoteId(id: String): EntityDirection?

    @Query("DELETE FROM entity_direction")
    suspend fun delRemote()
}