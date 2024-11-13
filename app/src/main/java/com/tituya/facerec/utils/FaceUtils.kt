package com.tituya.facerec.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceUtils {
    companion object {
        fun detectFaces(context: Context, uri: Uri, onFaceDetected: (MutableList<List<Int>>) -> Unit) {
            val imageInput = InputImage.fromFilePath(context, uri)

            val highAccuracy = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

            val detector = FaceDetection.getClient(highAccuracy)
            detector.process(imageInput)
                .addOnSuccessListener { faces ->
                    onFaceDetected(faces.map { face ->
                        val bounds = face.boundingBox
                        listOf(bounds.left, bounds.top, bounds.right, bounds.bottom)
                    }.toMutableList())
                }
                .addOnFailureListener { e ->
                }
        }

        fun getImageSize(context: Context, uri: Uri): Pair<Int, Int> {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri).use {
                BitmapFactory.decodeStream(it, null, options)
            }

            return if(options.outWidth > options.outHeight) {
                Pair(options.outHeight, options.outWidth)
            } else {
                Pair(options.outWidth, options.outHeight)
            }
        }
    }
}