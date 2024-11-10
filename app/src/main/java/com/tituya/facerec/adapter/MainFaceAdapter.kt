package com.tituya.facerec.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tituya.facerec.R

class MainFaceAdapter(private val dataSet: MutableList<String>) :
        RecyclerView.Adapter<MainFaceAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView

        init {
            imageView = view.findViewById(R.id.carousel_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_face_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.imageView.setImageResource(R.drawable.benj)
    }

    fun addFace(face: String) {
        dataSet.add(face)
        notifyItemRangeInserted(dataSet.size - 1, 1)
    }
}
