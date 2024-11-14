import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class OverlayRectangles (
    private val originalSize: Pair<Int, Int>,
    private val boundingFaces: MutableList<List<Int>>
) : BitmapTransformation() {
    val scaledBoundingFaces: MutableList<Rect> = mutableListOf()

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("OverlayRectangleTransformation".toByteArray())
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val bmp = Bitmap.createBitmap(toTransform)
        val canvas = Canvas(bmp).apply { drawBitmap(toTransform, 0f, 0f, null) }
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        // Scale the bounding boxes to the new image size
        val scale = toTransform.width.coerceAtLeast(toTransform.height).toFloat() / originalSize.second

        boundingFaces.forEach { rect ->
            paint.color = 0xFF0000
            paint.alpha = 120
            val adjustedRect = Rect(
                (rect[0] * scale).toInt(),
                (rect[1] * scale).toInt(),
                (rect[2] * scale).toInt(),
                (rect[3] * scale).toInt()
            )

            // Ensure the bounding box is within the image bounds
            if(adjustedRect.left < 0) adjustedRect.left = 0
            if(adjustedRect.top < 0) adjustedRect.top = 0
            if(adjustedRect.right > bmp.width) adjustedRect.right = bmp.width
            if(adjustedRect.bottom > bmp.height) adjustedRect.bottom = bmp.height

            scaledBoundingFaces.add(adjustedRect)
            canvas.drawRect(adjustedRect, paint)

        }

        return bmp
    }
}
