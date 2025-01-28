package com.tituya.facerec.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
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
import kotlin.math.pow
import kotlin.math.sqrt


class FaceUtils {
    companion object {
        fun detectFaces(context: Context, uri: Uri, onFaceDetected: (MutableList<List<Int>>) -> Unit) {
            val imageInput = InputImage.fromFilePath(context, uri)

            val highAccuracy = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
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

        fun fixImageOrientation(uri: Uri, context: Context): Bitmap {
            val inputStream = context.contentResolver.openInputStream(uri)

            // Lire les informations EXIF de l'image
            val exif = ExifInterface(inputStream!!)

            // Réinitialiser le InputStream pour décoder l'image
            inputStream.close() // Fermer le flux actuel
            val newInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(newInputStream)
            newInputStream!!.close()

            // Obtenir l'orientation EXIF
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

            // Appliquer la rotation ou la transformation si nécessaire
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            }

            // Retourner le bitmap corrigé si une transformation a été appliquée
            return if (matrix.isIdentity) {
                bitmap
            } else {
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
        }

        fun cropImageToFace(context: Context, uri: Uri, bounds: MutableList<List<Int>>): List<Bitmap> {
            val ret = mutableListOf<Bitmap>()
            for (i in bounds) {
                val left = i[0]
                val top = i[1]
                val right = i[2]
                val bottom = i[3]

                if(left < 0 || top < 0 || right < 0 || bottom < 0) continue
                ret.add(resizeBitmap(Bitmap.createBitmap(fixImageOrientation(uri, context), left, top, right - left, bottom - top), 160, 160))
            }
            return ret
        }

        fun calculateAverageEmbedding(embeddings: List<FloatArray>): FloatArray {
            if (embeddings.isEmpty()) {
                throw IllegalArgumentException("La liste des embeddings est vide.")
            }

            val embeddingSize = embeddings[0].size
            val avgEmbedding = FloatArray(embeddingSize) { 0f }

            for (embedding in embeddings) {
                for (i in embedding.indices) {
                    avgEmbedding[i] += embedding[i]
                }
            }

            for (i in avgEmbedding.indices) {
                avgEmbedding[i] = avgEmbedding[i] / embeddings.size
            }

            return avgEmbedding
        }

    }
}