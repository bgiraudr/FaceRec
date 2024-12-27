package com.tituya.facerec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.models.Person

class PersonAdapter(private val context: Context) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private val dataSet: MutableList<Person> = mutableListOf()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.personName)
        private val imageList: LinearLayout = view.findViewById(R.id.imageList)

        fun bind(person: Person) {
            textView.text = person.name
            imageList.removeAllViews()
            for (face in person.faces) {
                val imageView = ImageView(context)
                val layout = LinearLayout.LayoutParams(200, 200)
                layout.setMargins(5, 5, 5, 5)
                imageView.layoutParams = layout
                Glide.with(context)
                    .load(face)
                    .centerCrop()
                    .into(imageView)
                imageList.addView(imageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: PersonAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    fun addPerson(p: Person) {
        dataSet.add(p)
        notifyItemRangeInserted(dataSet.size - 1, 1)
    }
}