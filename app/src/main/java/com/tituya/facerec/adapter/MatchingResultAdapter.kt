package com.tituya.facerec.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.data.model.Face
import com.tituya.facerec.data.model.PairResults

class MatchingResultAdapter(private val matches: List<PairResults>) :
    RecyclerView.Adapter<MatchingResultAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.matchImageView)
        val scoreView: TextView = view.findViewById(R.id.matchScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match_find, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picturePath =  matches[position].picturePath
        val score = matches[position].score
        Glide.with(holder.itemView)
            .load(picturePath)
            .centerCrop()
            .into(holder.imageView)
        holder.scoreView.text = String.format("%.2f%%", score * 100)
    }

    override fun getItemCount(): Int = matches.size
}