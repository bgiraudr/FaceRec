package com.tituya.facerec.data.model

import com.tituya.facerec.data.room.FaceEntity
import java.nio.ByteBuffer


data class Face(
    val id: Long = 0L,
    val personId: Long,
    val embeddings: FloatArray,
    val faceImage: String
) {
    fun toEntity() = FaceEntity(id, personId, floatArrayToBlob(embeddings), faceImage)
    fun toEntity(personId: Long) = FaceEntity(id, personId, floatArrayToBlob(embeddings), faceImage)
    fun toEntity(id: Long, personId: Long) = FaceEntity(id, personId, floatArrayToBlob(embeddings), faceImage)

    private fun floatArrayToBlob(floatArray: FloatArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(floatArray.size * 4)
        for (value in floatArray) {
            byteBuffer.putFloat(value)
        }
        return byteBuffer.array()
    }
}
