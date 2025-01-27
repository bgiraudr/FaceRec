package com.tituya.facerec.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import java.util.UUID

@Dao
interface PersonDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(person: PersonEntity) : Long

    @Query("SELECT * FROM persons")
    fun getAllPersons(): List<PersonEntity>

    @Query("SELECT * FROM persons WHERE id = :personId")
    fun getPersonById(personId: Long): PersonEntity?

    @Transaction
    @Query("SELECT * FROM persons WHERE id = :personId")
    fun getPersonWithFaces(personId: Long): PersonWithFacesEntity

    @Transaction
    @Query("SELECT * FROM persons")
    fun getAllPersonsWithFaces(): List<PersonWithFacesEntity>

    @Delete
    fun deletePerson(person: PersonEntity)
}