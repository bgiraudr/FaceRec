package com.tituya.facerec.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.UUID

@Dao
interface FaceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFace(face: FaceEntity): Long

    @Query("SELECT * FROM faces WHERE personId = :personId")
    fun getFacesByPersonId(personId: Long): List<FaceEntity>

    @Query("SELECT * FROM faces WHERE id = :faceId")
    fun getFaceById(faceId: Long): FaceEntity?

    @Delete
    fun deleteFace(face: FaceEntity)
}