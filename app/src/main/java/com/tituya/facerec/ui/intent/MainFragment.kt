package com.tituya.facerec.ui.intent

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.adapter.SharedViewModel
import com.tituya.facerec.databinding.FragmentMainBinding

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

        sharedViewModel.selectedData.observe(viewLifecycleOwner) {
            if (firstFaceSelected != null && secondFaceSelected != null) {
                Toast.makeText(context, "Both faces are already selected", Toast.LENGTH_SHORT).show()
            } else {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.person_placeholder)
                    .circleCrop()
                    .into(if (firstFaceSelected == null) binding.face1 else binding.face2)
                if (firstFaceSelected == null) {
                    firstFaceSelected = it
                } else {
                    secondFaceSelected = it
                }
                if(firstFaceSelected != null && secondFaceSelected != null) {
                    binding.compareFaces.isEnabled = true
                }
            }
        }

        faceDeselected(binding.face1) {
            firstFaceSelected = null
            sharedViewModel.firstFaceDeselected()
            binding.compareFaces.isEnabled = false
        }

        faceDeselected(binding.face2) {
            secondFaceSelected = null
            binding.compareFaces.isEnabled = false
        }

        binding.compareFaces.setOnClickListener {
            if(firstFaceSelected != null && secondFaceSelected != null) {
                // todo compare faces
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