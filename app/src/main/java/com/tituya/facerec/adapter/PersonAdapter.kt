package com.tituya.facerec.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.data.model.Face
import com.tituya.facerec.data.model.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonAdapter(private val context: Context, private val onDeleteClick: (Person) -> Unit, private val onAddFaceClick: (Person) -> Unit, private val onLongFaceClick: (Person, Face) -> Unit) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private val dataSet: MutableList<Person> = mutableListOf()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.personName)
        private val imageList: LinearLayout = view.findViewById(R.id.imageList)
        private val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
        private val addFaceButton: ImageView = view.findViewById(R.id.addFaceButton)
        private val progress: ProgressBar = view.findViewById(R.id.progressBar)

        fun bind(personWithFaces: Person) {
            textView.text = personWithFaces.name
            imageList.removeAllViews()

            for (face in personWithFaces.faces) {
                val imageView = ImageView(context)
                val layout = LinearLayout.LayoutParams(200, 200)
                layout.setMargins(5, 5, 5, 5)
                imageView.layoutParams = layout
                Glide.with(context)
                    .load(face.faceImage) // Charger l'image depuis le chemin
                    .centerCrop()
                    .into(imageView)
                imageList.addView(imageView)

                imageView.setOnLongClickListener(View.OnLongClickListener {
//                    progress.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.IO).launch {
                        onLongFaceClick(personWithFaces, face)
                    }
                    true
                })
            }

            deleteButton.setOnClickListener {
                progress.visibility = View.VISIBLE
                onDeleteClick(personWithFaces)
                progress.visibility = View.INVISIBLE
            }

            addFaceButton.setOnClickListener {
                progress.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    onAddFaceClick(personWithFaces)
                    withContext(Dispatchers.Main) {
                        progress.visibility = View.INVISIBLE
                    }
                }
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

    fun deletePerson(p: Person) {
        val index = dataSet.indexOf(p)
        dataSet.removeAt(index)
        notifyItemRemoved(index)
    }

    fun updatePerson(p: Person) {
        val index = dataSet.indexOfFirst { it.id == p.id }
        dataSet[index] = p
        notifyItemChanged(index)
    }

    fun updateData(data: List<Person>) {
        dataSet.clear()
        dataSet.addAll(data)
        notifyDataSetChanged()
    }
}