package com.tituya.facerec.data.room

import androidx.room.Embedded
import androidx.room.Relation
import com.tituya.facerec.data.model.Person

data class PersonWithFacesEntity (
    @Embedded val person: PersonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    )
    val faces: List<FaceEntity>
) {
    fun toModel() = Person(person.id, person.name, faces.map { it.toModel() })
}