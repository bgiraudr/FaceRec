package com.tituya.facerec.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "persons")
data class PersonEntity (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)