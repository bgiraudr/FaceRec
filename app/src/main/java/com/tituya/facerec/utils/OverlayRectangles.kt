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
        val paint = Paint().apply { style = Paint.Style.FILL }

        // Scale the bounding boxes to the new image size
        val scale = toTransform.width.coerceAtLeast(toTransform.height).toFloat() / originalSize.second

        boundingFaces.forEach { rect ->
            paint.color = 0xFFFFFF
            paint.alpha = 120
            val adjustedRect = Rect(
                (rect[0] * scale).toInt(),
                (rect[1] * scale).toInt(),
                (rect[2] * scale).toInt(),
                (rect[3] * scale).toInt()
            )
            canvas.drawRect(adjustedRect, paint)
        }

        return bmp
    }
}
