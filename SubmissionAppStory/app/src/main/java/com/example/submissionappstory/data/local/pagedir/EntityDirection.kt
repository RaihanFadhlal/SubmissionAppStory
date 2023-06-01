package com.example.submissionappstory.data.local.pagedir

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entity_direction")
data class EntityDirection(
    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)