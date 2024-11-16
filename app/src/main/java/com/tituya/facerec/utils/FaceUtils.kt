package com.tituya.facerec.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import org.pytorch.executorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.FloatBuffer
import kotlin.math.pow
import kotlin.math.sqrt


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

        fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }

        fun computeEmbedding(context: Context?, bmp: Bitmap): FloatArray? {
            try {
                val m: Module = Module.load(getAssetFilePath(context!!, "facenet_xnn.pte"))
                val inputTensor: Tensor = bitmapToFloat32Tensor(bmp)
                val input1 = EValue.from(inputTensor)
                return m.forward(input1)[0].toTensor().dataAsFloatArray
            } catch (e: Exception) {
                Log.d("DEBUG", "compute: $e")
            }
            return null
        }

        private fun bitmapToFloat32Tensor(bitmap: Bitmap): Tensor {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val floatBuffer = Tensor.allocateFloatBuffer(3 * width * height)
            pixels.forEachIndexed { i, pixel ->
                floatBuffer.put(i, Color.red(pixel) / 255.0f) // R
                floatBuffer.put(width * height + i, Color.green(pixel) / 255.0f) // G
                floatBuffer.put(2 * width * height + i, Color.blue(pixel) / 255.0f) // B
            }

            return Tensor.fromBlob(floatBuffer, longArrayOf(1, 3, height.toLong(), width.toLong()))
        }

        fun cosineDistance(vecA: FloatArray, vecB: FloatArray): Double {
            require(vecA.size == vecB.size) { "Les vecteurs doivent avoir la même taille" }

            val dotProduct = vecA.zip(vecB).sumOf { (a, b) -> (a * b).toDouble() }
            val normA = sqrt(vecA.sumOf { it.toDouble().pow(2) })
            val normB = sqrt(vecB.sumOf { it.toDouble().pow(2) })

            return dotProduct / (normA * normB)
        }

        @Throws(IOException::class)
        fun getAssetFilePath(context: Context, assetName: String): String {
            val file = File(context.cacheDir, assetName)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }

            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buffer = ByteArray(4 * 1024) // 4 KB buffer
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }
            return file.absolutePath
        }
    }
}