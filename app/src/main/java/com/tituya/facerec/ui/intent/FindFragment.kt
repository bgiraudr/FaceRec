package com.tituya.facerec.ui.intent

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.adapter.SharedViewModel
import com.tituya.facerec.databinding.FragmentFindBinding

class FindFragment : Fragment() {
    private lateinit var binding: FragmentFindBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var firstFaceSelected: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel.firstFace.observe(viewLifecycleOwner, {
            if (firstFaceSelected != null) {
                 Toast.makeText(context, "Face is already selected", Toast.LENGTH_SHORT).show()
            } else {
                firstFaceSelected = it
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.person_placeholder)
                    .circleCrop()
                    .into(binding.face1)
            }
        })

        return view
    }
}