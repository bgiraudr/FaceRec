package com.tituya.facerec.ui.intent

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.adapter.SharedViewModel
import com.tituya.facerec.databinding.FragmentMainBinding
import com.tituya.facerec.utils.FaceUtils

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var firstFaceSelected: Bitmap? = null
    private var secondFaceSelected: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        observeFaceSelection(
            sharedViewModel.firstFace,
            {
                if(firstFaceSelected != null) false
                else {
                    firstFaceSelected = it
                    true
                }
            },
            binding.face1)

        observeFaceSelection(
            sharedViewModel.secondFace,
            {
                if(secondFaceSelected != null) false
                else {
                    secondFaceSelected = it
                    true
                }
            },
            binding.face2)

        faceDeselected(binding.face1) {
            firstFaceSelected = null
            sharedViewModel.firstFaceDeselected()
            binding.compareFaces.isEnabled = false
        }

        faceDeselected(binding.face2) {
            secondFaceSelected = null
            sharedViewModel.secondFaceDeselected()
            binding.compareFaces.isEnabled = false
        }

        binding.compareFaces.setOnClickListener {
            if(firstFaceSelected != null && secondFaceSelected != null) {
                val first = FaceUtils.resizeBitmap(firstFaceSelected!!, 160, 160)
                val snd = FaceUtils.resizeBitmap(secondFaceSelected!!, 160, 160)
                val firstEmbedding = FaceUtils.computeEmbedding(context, first)
                val secondEmbedding = FaceUtils.computeEmbedding(context, snd)
                if(firstEmbedding != null && secondEmbedding != null) {
                    val distance = FaceUtils.cosineDistance(firstEmbedding, secondEmbedding)
                    binding.similarity.text = "Similarity: ${"%.2f".format(distance)}"
                    val layoutParam = binding.similarityIndicator.layoutParams as ConstraintLayout.LayoutParams
                    val screenWidth = resources.displayMetrics.widthPixels // Largeur de l'écran
                    layoutParam.setMargins((((screenWidth-90)/2)+((screenWidth-90)/2)*distance).toInt(), 0, 0, 0)
                    binding.similarityIndicator.layoutParams = layoutParam
                    binding.similarityIndicatorImage.visibility = View.VISIBLE
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

    private fun observeFaceSelection(liveData: LiveData<Bitmap?>, onFaceSelected: (Bitmap?) -> Boolean, imageView: ImageView) {
        liveData.observe(viewLifecycleOwner) {
            if (onFaceSelected(it)) {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.person_placeholder)
                    .circleCrop()
                    .into(imageView)
            } else {
                Toast.makeText(context, "Face is already selected", Toast.LENGTH_SHORT).show()
            }
            if(firstFaceSelected != null && secondFaceSelected != null) {
                binding.compareFaces.isEnabled = true
            }
        }
    }
}