package com.tituya.facerec.ui.intent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.tituya.facerec.R
import com.tituya.facerec.adapter.SharedViewModel
import com.tituya.facerec.databinding.FragmentFindBinding

class FindFragment : Fragment() {
    private lateinit var binding: FragmentFindBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel.selectedData.observe(viewLifecycleOwner, {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.round)
                .circleCrop()
                .into(binding.face1)
        })

        return view
    }
}