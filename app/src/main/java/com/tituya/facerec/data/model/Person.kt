package com.tituya.facerec.data.model

import com.tituya.facerec.data.room.PersonEntity
import java.util.UUID

data class Person(
    val id: Long = 0L,
    val name: String,
    val faces: List<Face> = emptyList()
) {
    fun toEntity() = PersonEntity(name=name)
    fun toEntity(id:Long) = PersonEntity(id=id, name=name)
}