package com.tituya.facerec

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tituya.facerec.adapter.MatchingResultAdapter
import com.tituya.facerec.data.model.Face
import com.tituya.facerec.data.model.PairResults
import com.tituya.facerec.databinding.ActivityFindResultsBinding

class FindResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindResultsBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val matches: ArrayList<PairResults> = intent.getParcelableArrayListExtra("matches", PairResults::class.java)
            ?: ArrayList<PairResults>()
        val bmp: Bitmap = intent.getByteArrayExtra("facebmp")?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        val recyclerView = binding.matchesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MatchingResultAdapter(matches.sortedBy { it.score }.reversed())

        val count = matches.count { it.score > 0.5 }
        Glide.with(this).load(bmp).into(binding.faceToFind)
        binding.faceToFindText.text = "Found $count similar out of ${matches.size} faces"
    }
}