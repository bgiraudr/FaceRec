package com.tituya.facerec.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tituya.facerec.data.model.Face
import java.nio.ByteBuffer
import java.sql.Blob
import java.util.UUID

@Entity(
    tableName = "faces",
    foreignKeys = [ForeignKey(
        entity = PersonEntity::class,
        parentColumns = ["id"],
        childColumns = ["personId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["personId"])]
)
data class FaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val embeddings: ByteArray,
    val picturePath: String
) {
    fun toModel() = Face(id, personId, blobToFloatArray(embeddings), picturePath)

    private fun blobToFloatArray(blob: ByteArray): FloatArray {
        val byteBuffer = ByteBuffer.wrap(blob)
        val floatArray = FloatArray(blob.size / 4)
        for (i in floatArray.indices) {
            floatArray[i] = byteBuffer.getFloat()
        }
        return floatArray
    }
}