package com.tituya.facerec.ui.intent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tituya.facerec.databinding.FragmentFindBinding

class FindFragment : Fragment() {
    private lateinit var binding: FragmentFindBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}