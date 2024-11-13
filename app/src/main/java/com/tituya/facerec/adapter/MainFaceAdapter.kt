package com.tituya.facerec.adapter

import OverlayRectangles
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.utils.FaceUtils

class MainFaceAdapter :
        RecyclerView.Adapter<MainFaceAdapter.ViewHolder>() {

    private val dataSet: MutableList<FaceWithBounds> = mutableListOf()
    data class FaceWithBounds(val face: Uri, val originalSize: Pair<Int, Int>, val bounds: MutableList<List<Int>>)

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        var bounds: FaceWithBounds? = null

        init {
            imageView = view.findViewById(R.id.carousel_image_view)
            imageView.setOnClickListener { v -> onClick(v) }
        }

        fun bind(fullFace: FaceWithBounds) {
            if (bounds == null) bounds = fullFace
            Glide.with(itemView)
                .load(fullFace.face)
                .centerInside()
                .transform(OverlayRectangles(fullFace.originalSize, fullFace.bounds))
                .into(imageView)
        }

        private fun onClick(v: View?) {
            val gestureDetector = GestureDetector(v?.context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent) {
                    val x = e.x.toInt()
                    val y = e.y.toInt()
                    Log.d("MainFaceAdapter", "Image long-pressed at position $adapterPosition, x: $x, y: $y")
                    val scale = imageView.width.coerceAtLeast(imageView.height).toFloat() / bounds!!.originalSize.second

                    for(rect in bounds!!.bounds) {
                        if(x >= rect[0]*scale && x <= rect[2]*scale && y >= rect[1]*scale && y <= rect[3]*scale) {
                            Toast.makeText(v!!.context, "YO !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    return false
                }
            })

            v?.setOnTouchListener { view, event ->
                gestureDetector.onTouchEvent(event)
                view.performClick()
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
}
