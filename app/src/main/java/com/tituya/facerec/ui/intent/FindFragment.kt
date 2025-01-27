package com.tituya.facerec.ui.intent

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.tituya.facerec.FindResultsActivity
import com.tituya.facerec.ManageFacesActivity
import com.tituya.facerec.R
import com.tituya.facerec.adapter.SharedViewModel
import com.tituya.facerec.data.model.Face
import com.tituya.facerec.data.model.PairResults
import com.tituya.facerec.data.model.Person
import com.tituya.facerec.data.room.FaceDAO
import com.tituya.facerec.data.room.FaceRecDatabase
import com.tituya.facerec.data.room.PersonDAO
import com.tituya.facerec.databinding.FragmentFindBinding
import com.tituya.facerec.utils.FaceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class FindFragment : Fragment() {
    private lateinit var binding: FragmentFindBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var firstFaceSelected: Bitmap? = null
    private lateinit var database: FaceRecDatabase
    private lateinit var faceDao: FaceDAO
    private lateinit var personDao: PersonDAO
    private var intentMatch: Intent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FaceRecDatabase.getDatabase(requireContext())
        personDao = database.personDao()
        faceDao = database.faceDao()

        sharedViewModel.firstFace.observe(viewLifecycleOwner) {
            if (firstFaceSelected != null) {
                Toast.makeText(context, "Face is already selected", Toast.LENGTH_SHORT).show()
            } else {
                firstFaceSelected = it
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.person_placeholder)
                    .circleCrop()
                    .into(binding.face1)
                binding.findButton.isEnabled = true
            }
        }

        binding.manageFacesButton.setOnClickListener {
            startActivity(Intent(requireContext(), ManageFacesActivity::class.java))
        }

        binding.similarityLayout.setOnClickListener {
            intentMatch?.let { it1 -> startActivity(it1) }
        }

        faceDeselected(binding.face1) {
            firstFaceSelected = null
            sharedViewModel.firstFaceDeselected()
            binding.findButton.isEnabled = false
        }

        binding.findButton.setOnClickListener {
            if (firstFaceSelected != null) {
                val first = FaceUtils.resizeBitmap(firstFaceSelected!!, 160, 160)
                val firstEmbedding = FaceUtils.computeEmbedding(context, first)
                if (firstEmbedding != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        // Récupération de toutes les personnes avec leurs visages
                        val persons = personDao.getAllPersonsWithFaces()
                        var bestDistance = Double.MIN_VALUE // Distance minimale
                        var bestPerson: Person? = null

                        // Calcul de la distance pour chaque visage
                        val sortedMatches = arrayListOf<PairResults>()
                        for (person in persons) {
//                            val avgEmbd = FaceUtils.calculateAverageEmbedding(person.toModel().faces.map { it.embeddings })
                            for (face in person.faces) {
                                val distance = FaceUtils.cosineDistance(firstEmbedding, face.toModel().embeddings)
                                sortedMatches.add(PairResults(face.picturePath, distance))
                                if (distance > bestDistance) {
                                    bestDistance = distance
                                    bestPerson = person.toModel()
                                }
                            }
                        }

                        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
                        first.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        intentMatch = Intent(context, FindResultsActivity::class.java).apply {
                            putParcelableArrayListExtra("matches", sortedMatches)
                            putExtra("facebmp", stream.toByteArray())
                        }

                        // Mise à jour de l'interface utilisateur
                        withContext(Dispatchers.Main) {
                            if (bestPerson != null && bestDistance > 0.5) {
                                // Afficher la personne la plus proche (par exemple dans un TextView)
                                binding.personNameValue.text = String.format("%.2f%%", bestDistance * 100.0f)
                                binding.personName.text = bestPerson.name
                            } else {
                                binding.personNameValue.text = "No match found"
                                binding.personName.text = ""
                            }
                        }
                    }
                }
            }
        }

        return view
    }

    private fun faceDeselected(imageView: ImageView, onDeselected: () -> Unit) {
        imageView.setOnClickListener {
            onDeselected()
            Glide.with(this)
                .load(R.drawable.person_placeholder)
                .circleCrop()
                .into(imageView)
        }
    }
}