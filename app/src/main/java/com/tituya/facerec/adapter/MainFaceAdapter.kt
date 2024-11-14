package com.tituya.facerec.adapter

import OverlayRectangles
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.utils.FaceUtils

class MainFaceAdapter(private val sharedViewModel: SharedViewModel) :
        RecyclerView.Adapter<MainFaceAdapter.ViewHolder>() {

    data class FaceWithBounds(val face: Uri, val originalSize: Pair<Int, Int>, val bounds: MutableList<List<Int>>)

    private val dataSet: MutableList<FaceWithBounds> = mutableListOf()

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        private lateinit var overlayRectangles: OverlayRectangles

        init {
            imageView = view.findViewById(R.id.carousel_image_view)
            imageView.setOnClickListener { v -> onClick(v) }
        }

        fun bind(fullFace: FaceWithBounds) {
            overlayRectangles = OverlayRectangles(fullFace.originalSize, fullFace.bounds)

            Glide.with(itemView)
                .load(fullFace.face)
                .centerInside()
                .transform(overlayRectangles)
                .into(imageView)
        }

        private fun onClick(v: View?) {
            val gestureDetector = GestureDetector(v?.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent) {
                    println(overlayRectangles.scaledBoundingFaces)
                    if(!::overlayRectangles.isInitialized) return

                    val x = e.x.toInt()
                    val y = e.y.toInt()

                    for(rect: Rect in overlayRectangles.scaledBoundingFaces) {
                        if(x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom) {
                            sharedViewModel.faceSelected(Bitmap.createBitmap(imageView.drawable.toBitmap(), rect.left, rect.top, rect.width(), rect.height()))
                            vibrate(v?.context!!)
                            break
                        }
                    }
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    return false
                }
            })

            v?.setOnTouchListener { view, event ->
                gestureDetector.onTouchEvent(event)
                if(event.action == MotionEvent.ACTION_UP) view.performClick()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_face_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    fun addFaceWithBounds(context:Context, face: Uri, bounds: MutableList<List<Int>>) {
        dataSet.add(FaceWithBounds(face, FaceUtils.getImageSize(context, face), bounds))
        notifyItemRangeInserted(dataSet.size - 1, 1)
    }

    fun vibrate(context: Context, duration: Long = 100) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
